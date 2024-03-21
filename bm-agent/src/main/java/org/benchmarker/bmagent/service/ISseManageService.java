package org.benchmarker.bmagent.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.benchmarker.bmagent.consts.SseManageConsts;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Interface for the SSE service
 */
public interface ISseManageService extends SseManageConsts {
    Map<Long, SseEmitter> sseEmitterHashMap = new ConcurrentHashMap<>();

    /**
     * Start a new SSE emitter for the given id
     *
     * @param id Long
     * @return SseEmitter
     */
    SseEmitter start(Long id);

    /**
     * Stop the SSE emitter for the given id
     *
     * @param id Long
     */
    void stop(Long id);

    /**
     * Send a message to the SSE emitter for the given id
     *
     * @param id Long
     * @param message string
     */
    void send(Long id, Object message);
}
