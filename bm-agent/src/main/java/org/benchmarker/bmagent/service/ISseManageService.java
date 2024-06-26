package org.benchmarker.bmagent.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.benchmarker.bmagent.consts.SseManageConsts;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Interface for the SSE service
 */
public interface ISseManageService extends SseManageConsts {

    Map<Long, SseEmitter> sseEmitterHashMap = new ConcurrentHashMap<>();

    /**
     * Start a new SSE emitter for the given id
     *
     * @param id           Long
     * @param templateInfo TemplateInfo
     * @return SseEmitter
     */
    SseEmitter start(Long id, String groupId, TemplateInfo templateInfo);

    /**
     * Stop the SSE emitter for the given id
     *
     * @param id Long
     */
    void stop(Long id);

    /**
     * Send a message to the SSE emitter for the given id
     *
     * @param id      Long
     * @param message string
     */
    void send(Long id, Object message);

    /**
     *
     *
     * @param id
     * @throws IOException
     */
    void stopSign(Long id) throws IOException;
}
