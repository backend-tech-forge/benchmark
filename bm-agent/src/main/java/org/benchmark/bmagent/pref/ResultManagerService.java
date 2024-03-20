package org.benchmark.bmagent.pref;

import java.util.concurrent.ConcurrentHashMap;
import org.benchmark.bmagent.service.MapManager;
import org.benchmark.dto.TestResult;
import org.springframework.stereotype.Component;

/**
 * ResultManagerService
 */
@Component
public class ResultManagerService implements MapManager<Long, TestResult> {

    // 여기에  TestResult 를 저장하고, 스케줄러에서 결과를 가져다 씁니다.
    private final ConcurrentHashMap<Long, TestResult> resultHashMap = new ConcurrentHashMap<>();

    @Override
    public TestResult getResult(Long templateId) {
        if (resultHashMap.containsKey(templateId)) {
            return resultHashMap.get(templateId);
        }
        return null;
    }

    @Override
    public void addResult(Long templateId, TestResult result) {
        resultHashMap.put(templateId, result);
    }
}
