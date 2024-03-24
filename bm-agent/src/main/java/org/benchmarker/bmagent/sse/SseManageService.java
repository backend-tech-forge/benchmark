package org.benchmarker.bmagent.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.pref.ResultManagerService;
import org.benchmarker.bmagent.schedule.ScheduledTaskService;
import org.benchmarker.bmagent.service.AbstractSseManageService;
import org.benchmarker.bmagent.service.IScheduledTaskService;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.benchmarker.bmagent.common.AgentUtils.*;

/**
 * SseManageService for managing SseEmitter
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SseManageService extends AbstractSseManageService {

    private final IScheduledTaskService scheduledTaskService;

    private final ResultManagerService resultManagerService;

    private final WebClient webClient;

    private AtomicInteger totalRequests = new AtomicInteger(0);

    /**
     * Start the SseEmitter for the given id and return the SseEmitter
     *
     * <p>Emits TestResult to id in every 1 second by running {@link ScheduledTaskService#start}
     *
     * @param id Long
     * @return SseEmitter
     * @see ScheduledTaskService
     */
    @Override
    public SseEmitter start(Long id, TemplateInfo templateInfo) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // when the client disconnects, complete the SseEmitter
        alwaysDoStop(id, emitter);

        if (sseEmitterHashMap.containsKey(id)) {
            log.debug("SSE already exists for template ID: {}", id);
            return null;
        }

        // Save the SseEmitter to the map
        sseEmitterHashMap.put(id, emitter);
//        resultManagerService.save(id, new TestResult());

        /**
         * TODO:DEV Target Server 에 HTTP 요청 시작 메소드 작성
         *
         * TemplateInfo 에 기반하여 Target Server 에 HTTP 요청을 시작하는 메소드를 여기에 작성하시면 됩니다 :)
         *
         */

        // 비동기 처리 후 각각의 결과를 담은 객체 반환
        Mono<CommonTestResult> saveTestResultMono = createAndProcessRequest(webClient, templateInfo).log();

        CommonTestResult test = saveTestResultMono.block();
        resultManagerService.save(id, test);

//        saveTestResultMono.subscribe(testResult -> {
//            log.info("Target Server Result received: " + testResult);
//            resultManagerService.save(id, testResult);
//        }, error -> {
//            log.info("Target Sever Error occurred: " + error.getMessage());
//        });

        // 1초마다 TestResult 를 보내는 스케줄러 시작
        scheduledTaskService.start(id, () -> {
            send(id, resultManagerService.find(id));
        }, 0, 1, TimeUnit.SECONDS);

        return emitter;
    }

    private Mono<CommonTestResult> createAndProcessRequest(WebClient webClient, TemplateInfo templateInfo) {

        long startTime = System.currentTimeMillis();
        totalRequests.incrementAndGet();
        Mono<ResponseEntity<String>> resultMono = createRequest(webClient, templateInfo);

        return resultMono.publishOn(Schedulers.boundedElastic()).flatMap(response -> {
            long endTime = System.currentTimeMillis();

            HttpStatusCode statusCode = response.getStatusCode();
            boolean isSuccess = false;
            boolean isError = false;

            if (statusCode.is2xxSuccessful()) {
                isSuccess = true;
            } else if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
                isError = true;
            }

            int success = isSuccess ? 1 : 0;
            int error = isError ? 1 : 0;

            double tpsAvgTime = calculateTPS(startTime, endTime, totalRequests.get());
            double avgResponseTime = calculateAvgResponseTime(startTime, endTime, totalRequests.get());

            CommonTestResult commonTestResult = CommonTestResult.builder()
                    .testId(templateInfo.getId())
                    .startedAt(String.valueOf(startTime))
                    .finishedAt(String.valueOf(endTime))
                    .url(templateInfo.getUrl())
                    .method(templateInfo.getMethod())
                    .totalRequests(totalRequests.get())
                    .totalSuccess(success)
                    .totalErrors(error)
                    .statusCode(statusCode.value())
                    .statusCodeCount(new HashMap<>())
                    .totalUsers(totalRequests.get())
                    .totalDuration("-")
                    .MTTFBPercentiles(new HashMap<>())
                    .tpsAverage(tpsAvgTime)
                    .TPSPercentiles(new HashMap<>())
                    .mttfbAverage(avgResponseTime)
                    .build();

            return Mono.just(commonTestResult);

        }).onErrorResume(throwable -> {
            log.error("Error occurred: " + throwable.getMessage());

            long endTime = System.currentTimeMillis();

            CommonTestResult defaultValue = CommonTestResult.builder()
                    .startedAt(String.valueOf(startTime))
                    .finishedAt(String.valueOf(endTime))
                    .url(templateInfo.getUrl())
                    .method(templateInfo.getMethod())
                    .totalRequests(totalRequests.get())
                    .totalSuccess(0)
                    .totalErrors(1)
                    .statusCode(500)
                    .statusCodeCount(new HashMap<>())
                    .tpsAverage(0)
                    .MTTFBPercentiles(new HashMap<>())
                    .TPSPercentiles(new HashMap<>())
                    .totalUsers(totalRequests.get())
                    .totalDuration("-")
                    .mttfbAverage(0)
                    .build();

            return Mono.just(defaultValue);
        });
    }

    /**
     * Stop the SseEmitter for the given id
     *
     * <p>Shutdown the {@link ScheduledTaskService} and remove the SseEmitter from the map
     *
     * @param id Long
     */
    @Override
    public void stop(Long id) {
        SseEmitter emitter = sseEmitterHashMap.remove(id);
        if (emitter != null) {
            log.info("remove sse emitter");
            this.send(id, "SSE completed");
            totalRequests.set(0);
            emitter.complete();
        }
        scheduledTaskService.shutdown(id);
    }

    @Override
    public void send(Long id, Object data) {
        try {
            SseEmitter emitter = sseEmitterHashMap.get(id);
            if (emitter != null) {
                emitter.send(data);
            }
        } catch (IOException e) {
            log.warn("IOException");
        }
    }

    /**
     * Always do stop for the given id and emitter
     *
     * @param id      Long
     * @param emitter SseEmitter
     */
    private void alwaysDoStop(Long id, SseEmitter emitter) {
        emitter.onCompletion(() -> {
            log.info("SSE Completed");
            this.stop(id);
        });
        emitter.onTimeout(() -> {
            log.warn("SSE Timeout");
            this.stop(id);
        });
        emitter.onError((ex) -> {
            log.info("SSE connection error for template ID: {}", id);
            this.stop(id); // Call method to clean up resources
        });
    }

}