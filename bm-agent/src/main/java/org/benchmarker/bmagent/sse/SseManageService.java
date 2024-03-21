package org.benchmarker.bmagent.sse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmagent.pref.ResultManagerService;
import org.benchmarker.bmagent.schedule.ScheduledTaskService;
import org.benchmarker.bmagent.service.AbstractSseManageService;
import org.benchmarker.bmagent.service.IScheduledTaskService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SseManageService for managing SseEmitter
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SseManageService extends AbstractSseManageService {

    private final IScheduledTaskService scheduledTaskService;
    private final ResultManagerService resultManagerService;

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
    public SseEmitter start(Long id) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        // when the client disconnects, complete the SseEmitter
        alwaysDoStop(id, emitter);

        if (sseEmitterHashMap.containsKey(id)) {
            log.debug("SSE already exists for template ID: {}", id);
            return null;
        }

        // Save the SseEmitter to the map
        sseEmitterHashMap.put(id, emitter);

        // 1초마다 TestResult 를 보내는 스케줄러 시작
        scheduledTaskService.start(id, () -> {
            send(id, resultManagerService.find(id));
        }, 0, 1, TimeUnit.SECONDS);

        return emitter;
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