package org.benchmark.bmagent.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public abstract class AbstractSseManageService implements ISseManageService {
    protected Map<Long, SseEmitter> sseEmitterHashMap = new ConcurrentHashMap<>();

}
