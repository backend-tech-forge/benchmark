package org.benchmarker.bmagent.pref;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.service.IScheduledTaskService;
import org.benchmarker.bmagent.util.WebClientSupport;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import reactor.core.publisher.Mono;

/**
 * High load HTTP sender
 */
@Slf4j
@Getter
public class HttpSender {

    private final ResultManagerService resultManagerService;
    private final IScheduledTaskService scheduledTaskService;


    public HttpSender(ResultManagerService resultManagerService,
        IScheduledTaskService scheduledTaskService) {
        this.resultManagerService = resultManagerService;
        this.scheduledTaskService = scheduledTaskService;
    }

    private AtomicInteger totalRequests = new AtomicInteger(0);
    private AtomicInteger totalSuccess = new AtomicInteger(0);
    private AtomicInteger totalErrors = new AtomicInteger(0);
    // Response time, TPS
    private AtomicInteger tps = new AtomicInteger(0);
    private ConcurrentHashMap<LocalDateTime, Double> tpsMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<LocalDateTime, Long> mttfbMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> statusCodeCount = new ConcurrentHashMap<>();
    private List<CompletableFuture<Void>> futures;
    private Boolean isRunning=true;

    /**
     * Send multiple requests to target server
     *
     * <p>This method run child scheduler with {@link TemplateInfo#id} key</p>
     *
     * @param templateInfo
     */
    public void sendRequests(TemplateInfo templateInfo) throws MalformedURLException {
        URL url = new URL(templateInfo.getUrl());
        RequestHeadersSpec<?> req = WebClientSupport.create(templateInfo.getMethod(),
            templateInfo.getUrl(),
            templateInfo.getBody(),
            templateInfo.getHeaders());
        // Future 설정
        futures = IntStream.range(0, templateInfo.getVuser())
            .mapToObj(i -> CompletableFuture.runAsync(() -> {
                for (int j = 0; j < templateInfo.getMaxRequest(); j++) {
                    if (!isRunning){
                        return;
                    }

                    long requestStartTime = System.currentTimeMillis();  // 요청 시작 시간 기록
                    req.exchangeToMono(resp -> {
                        String statusCode = resp.statusCode().toString();
                        statusCodeCount.merge(statusCode, 1, Integer::sum);
                        if (resp.statusCode().is2xxSuccessful()) {
                            totalSuccess.incrementAndGet();
                        } else {
                            totalErrors.incrementAndGet();
                        }
                        return Mono.empty();
                    }).block();

                    long requestEndTime = System.currentTimeMillis(); // 요청 종료 시간 기록
                    long elapsedTime = requestEndTime - requestStartTime; // 요청 지연 시간 계산
                    LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
                    mttfbMap.merge(currentTime, elapsedTime, (oldValue, newValue) -> (oldValue + newValue) / 2); // 1초 간격 time series로 계산된 평균 지연 시간 저장
                    tps.incrementAndGet();
                    totalRequests.incrementAndGet();
                }
            }))
            .toList();

        // Need to calculate & save TPS and MTTFB in every 1 second.
        scheduledTaskService.startChild(Long.valueOf(templateInfo.getId()),"recorder", () -> {
            // save current tps & reset
            tpsMap.put(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), Double.valueOf(tps.get()));
            tps.set(0);
        }, 0, 1, java.util.concurrent.TimeUnit.SECONDS);

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     * Calculate percentile for TPS (Transactions Per Second)
     *
     * @param percentile The percentile to calculate (e.g., 90 for 90th percentile)
     * @return The calculated percentile value
     */
    public Map<Double,Double> calculateTpsPercentile(List<Double> percentile) {
        Map<Double,Double> result = new HashMap<>();

        List<Double> tpsList = this.sortedTpsMap();
        int size = tpsList.size();
        for (Double p : percentile){
            int index = (int) Math.ceil((p / 100) * size) - 1;
            if (index < 0) {
                return Map.of(0D,0D); // 예외처리: 인덱스가 음수인 경우
            }
            result.put(p,tpsList.get(index));
        }

        return result;
    }

    public List<Double> sortedTpsMap() {
        Map<LocalDateTime, Double> tpsSnapshot = new ConcurrentHashMap<>(tpsMap);
        return tpsSnapshot.values().stream().sorted(Comparator.reverseOrder()).toList(); // <-- Here STOP!
    }
    public List<Long> sortedMttfbMap() {
        Map<LocalDateTime, Long> tpsSnapshot = new ConcurrentHashMap<>(mttfbMap);
        return tpsSnapshot.values().stream().sorted().toList(); // <-- Here STOP!
    }

    /**
     * Calculate percentile for MTTFB (Mean Time To First Byte)
     *
     * @param percentile The percentile to calculate (e.g., 90 for 90th percentile)
     * @return The calculated percentile value
     */
    public Map<Double,Double> calculateMttfbPercentile(List<Double> percentile) {
        Map<Double,Double> result = new HashMap<>();

        List<Long> mttfbList = this.sortedMttfbMap();
        int size = mttfbList.size();
        for (Double p : percentile){
            int index = (int) Math.ceil((p / 100) * size) - 1;
            if (index < 0) {
                return Map.of(0D,0D); // 예외처리: 인덱스가 음수인 경우
            }
            result.put(p, Double.valueOf(mttfbList.get(index)));
        }

        return result;
    }

    /**
     * downstream 에서만 제거 가능. 외부에서는 cancel 불가능...
     */
    public void cancelRequests() {
        isRunning=false;

    }

}