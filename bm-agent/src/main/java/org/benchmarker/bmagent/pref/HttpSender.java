package org.benchmarker.bmagent.pref;

import static org.benchmarker.bmcommon.util.NoOp.noOp;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.AgentStatus;
import org.benchmarker.bmagent.consts.PreftestConsts;
import org.benchmarker.bmagent.pref.calculate.IResultCalculator;
import org.benchmarker.bmagent.pref.calculate.ResultCalculator;
import org.benchmarker.bmagent.service.IScheduledTaskService;
import org.benchmarker.bmagent.status.AgentStatusManager;
import org.benchmarker.bmagent.util.WebClientSupport;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Mono;

/**
 * High load HTTP sender
 * <p>
 * This class represents a high load HTTP sender responsible for sending multiple requests to a
 * target server. It contains functionality to 1) manage request sending, 2) calculate statistics
 * such as Transactions Per Second (TPS) and Mean Time To First Byte (MTTFB), and 3) cancel ongoing
 * requests.
 * </p>
 * <p>
 * All request will be running in {@link CompletableFuture} with common-pool. And threads will be
 * created according to the number of {@code vuser}
 * </p>
 *
 * @author Gyumin Hwangbo
 * @since 2024-03-30
 */
@Slf4j
@Getter
public class HttpSender {

    private final ResultManagerService resultManagerService;
    private final IScheduledTaskService scheduledTaskService;

    private final AgentStatusManager agentStatusManager;

    private IResultCalculator resultCalculator = new ResultCalculator();
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public HttpSender(ResultManagerService resultManagerService,
        IScheduledTaskService scheduledTaskService, AgentStatusManager agentStatusManager) {
        this.resultManagerService = resultManagerService;
        this.scheduledTaskService = scheduledTaskService;
        this.agentStatusManager = agentStatusManager;
    }

    private Integer defaultMaxRequestsPerUser = Integer.MAX_VALUE;
    private Integer defaultMaxDuration = 5; // 5 hours
    private AtomicInteger totalRequests = new AtomicInteger(0);
    private AtomicInteger totalSuccess = new AtomicInteger(0);
    private AtomicInteger totalErrors = new AtomicInteger(0);
    private Double tpsAvg = 0D;
    private Double mttfbAvg = 0D;
    // Response time, TPS
    private AtomicInteger tps = new AtomicInteger(0);
    private ConcurrentHashMap<LocalDateTime, Double> tpsMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<LocalDateTime, Long> mttfbMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> statusCodeCount = new ConcurrentHashMap<>();
    private List<CompletableFuture<Void>> futures;
    private Boolean isRunning = true;
    private Boolean isErrorExceed = false;
    private Boolean isStopped = false;

    /**
     * <strong>Major implementation sending multiple requests to target server</strong>
     *
     * <p>Whenever totalRequests hit maxRequests, test stop even if duration is still remained.</p>
     * <p>But, if you has null value of maxRequests and valid maxDuration, it will continue to run
     * test until current time is reach to maxDuration </p>
     *
     * <p>This method run child scheduler with {@link TemplateInfo#id} key</p>
     *
     * @param templateInfo {@link TemplateInfo}
     */
    public void sendRequests(SseEmitter sseEmitter, TemplateInfo templateInfo)
        throws MalformedURLException {

        URL url = new URL(templateInfo.getUrl());
        RequestHeadersSpec<?> req = WebClientSupport.create(templateInfo.getMethod(),
            templateInfo.getUrl(),
            templateInfo.getBody(),
            templateInfo.getHeaders());

        // if both duration & requests is not valid, immediately return
        if (templateInfo.getMaxDuration() == null && templateInfo.getMaxRequest() == null) {
            return;
        } else if (templateInfo.getMaxRequest() != null && templateInfo.getMaxDuration() == null) {
            templateInfo.setMaxDuration(Duration.ofHours(defaultMaxDuration));
        } else if (templateInfo.getMaxRequest() == null && templateInfo.getMaxDuration() != null) {
            templateInfo.setMaxRequest(defaultMaxRequestsPerUser);
        } else {
            noOp();
        }

        Duration duration = templateInfo.getMaxDuration();
        log.info("Now send multiple HTTP request to target server");
        log.info(templateInfo.toString());

        // Future setup
        futures = IntStream.range(0, templateInfo.getVuser())
            .mapToObj(i -> CompletableFuture.runAsync(() -> {
                long startTime = System.currentTimeMillis(); // 시작 시간 기록
                long endTime = startTime + duration.toMillis();

                for (int j = 0; j < templateInfo.getMaxRequest(); j++) {
                    // 테스트 시간 종료
                    if (System.currentTimeMillis() > endTime){
                        break;
                    }
                    // 만약 running 이 아니거나 시간이 끝났다면, 에러율이 너무 높다면
                    if (!isRunning || isErrorExceed) {
                        agentStatusManager.updateAgentStatus(AgentStatus.READY);
                        break;
                    }
                    long requestStartTime = System.currentTimeMillis();  // 요청 시작 시간 기록
                    req.exchangeToMono(resp -> {
                        String statusCode = resp.statusCode().toString();
                        statusCodeCount.merge(statusCode, 1, Integer::sum);
                        if (resp.statusCode().is2xxSuccessful()) {
                            totalSuccess.incrementAndGet();
                            tps.incrementAndGet();
                        } else {
                            totalErrors.incrementAndGet();
                        }
                        return Mono.empty();
                    }).block();

                    long requestEndTime = System.currentTimeMillis();
                    long elapsedTime = requestEndTime - requestStartTime;
                    LocalDateTime currentTime = LocalDateTime.now()
                        .truncatedTo(ChronoUnit.SECONDS);
                    mttfbMap.merge(currentTime, elapsedTime,
                        (oldValue, newValue) -> (oldValue + newValue) / 2);
                    totalRequests.incrementAndGet();
                }

            }))
            .toList();

        // Need to calculate & save TPS and MTTFB in every 1 second.
        scheduledTaskService.startChild(Long.valueOf(templateInfo.getId()), "recorder", () -> {
            LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            // save current tps & reset
            tpsMap.put(currentTime, Double.valueOf(tps.get()));
            saveAverage(currentTime);
            tps.set(0); // initial
        }, 0, 1, java.util.concurrent.TimeUnit.SECONDS);

        // error observer
        scheduledTaskService.startChild(Long.valueOf(templateInfo.getId()), "error-observer",
            () -> {
                int requests = totalRequests.get();
                int errors = totalErrors.get();
                if (requests != 0 && errors != 0) {
                    // if error rate exceed 50%, order future to stop!
                    if ((double) errors / requests * 100 > PreftestConsts.errorLimitRate) {
                        log.warn("Template-{}, error rate exceed {}", templateInfo.getId(),
                            PreftestConsts.errorLimitRate);
                        isErrorExceed=true;
                    }
                }
            }, PreftestConsts.errorLimitCheckDelay, PreftestConsts.errorLimitCheckPeriod,
            java.util.concurrent.TimeUnit.SECONDS);

        // CompletableFuture 종료까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * Calculate percentile for TPS (Transactions Per Second)
     *
     * @param percentile The percentile to calculate (e.g., 90 for 90th percentile)
     * @return The calculated percentile value
     */
    public Map<Double, Double> calculateTpsPercentile(List<Double> percentile) {
        return resultCalculator.percentile(tpsMap, percentile, true);
    }

    /**
     * Calculate percentile for MTTFB (Mean Time To First Byte)
     *
     * @param percentile The percentile to calculate (e.g., 90 for 90th percentile)
     * @return The calculated percentile value
     */
    public Map<Double, Long> calculateMttfbPercentile(List<Double> percentile) {
        return resultCalculator.percentile(mttfbMap, percentile, false);
    }

    /**
     * Save current TPS, MTTFB average
     *
     * @param currentTime LocalDateTime
     */
    public void saveAverage(LocalDateTime currentTime) {
        Double currentTps = (double) tps.get();
        Long currentMttfb = mttfbMap.get(currentTime);

        if (tpsAvg == 0) {
            tpsAvg = currentTps;
        } else {
            currentTps = resultCalculator.average(tpsAvg, currentTps);
            tpsAvg = Double.parseDouble(decimalFormat.format(currentTps));
        }
        if (currentMttfb != null) {
            Double doubleMTTFB = Double.valueOf(currentMttfb);
            if (mttfbAvg == 0) {
                mttfbAvg = doubleMTTFB;
            } else {
                Double average = resultCalculator.average(mttfbAvg, doubleMTTFB);
                mttfbAvg = Double.parseDouble(decimalFormat.format(average));
            }
        }

    }

    /**
     * downstream 에서만 제거 가능. 외부에서는 cancel 불가능...
     */
    public void cancelRequests() {
        log.info("cancel requests");
        isRunning = false;

    }

}
