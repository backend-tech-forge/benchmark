package org.benchmarker.bmagent.pref;

import java.util.concurrent.ConcurrentHashMap;
import org.benchmarker.bmagent.service.MapManager;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmagent.service.ISseManageService;
import org.springframework.stereotype.Component;

/**
 * ResultManagerService for managing TestResult
 *
 * <p>
 * After bmagent send multiple HTTP request to target server, calculated delays and others things need
 * to be added in resultHashMap
 * </p>
 *
 * <p>
 * resultHashMap will be used by {@link ISseManageService} to send
 * TestResult to client
 * </p>
 *
 * @see ISseManageService
 */
@Component
public class ResultManagerService implements MapManager<Long, CommonTestResult> {

    /**
     * ConcurrentHashMap for storing TestResult by its id
     */
    private final ConcurrentHashMap<Long, CommonTestResult> resultHashMap = new ConcurrentHashMap<>();

    /**
     * Find TestResult from resultHashMap
     *
     * @param id {@link Long}
     * @return {@link CommonTestResult} or null
     */
    @Override
    public CommonTestResult find(Long id) {
        if (resultHashMap.containsKey(id)) {
            return resultHashMap.get(id);
        }
        return null;
    }

    /**
     * Save TestResult to resultHashMap
     * @param id {@link Long}
     * @param object {@link CommonTestResult}
     */
    @Override
    public void save(Long id, CommonTestResult object) {
        resultHashMap.put(id, object);
    }

    /**
     * Remove TestResult from resultHashMap
     * @param id {@link Long}
     */
    @Override
    public void remove(Long id) {
        resultHashMap.remove(id);
    }
}
