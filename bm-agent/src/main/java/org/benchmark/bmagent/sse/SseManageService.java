package org.benchmark.bmagent.sse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmark.bmagent.schedule.ScheduledTaskService;
import org.benchmark.bmagent.service.AbstractSseManageService;
import org.benchmark.util.RandomUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Slf4j
@RequiredArgsConstructor
public class SseManageService extends AbstractSseManageService {

    private final ScheduledTaskService scheduledTaskService;

    @Override
    public SseEmitter start(Long id) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        alwaysDoStop(id, emitter);

        if (sseEmitterHashMap.containsKey(id)) {
            log.debug("SSE already exists for template ID: {}", id);
            return null;
        }
        sseEmitterHashMap.put(id, emitter);

        scheduledTaskService.start(id, () -> {
            log.info("SSE send");
            send(id, RandomUtils.generateRandomTestResult());
        }, 0, 1, TimeUnit.SECONDS);

        return emitter;
    }

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
     * @param id     Long
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