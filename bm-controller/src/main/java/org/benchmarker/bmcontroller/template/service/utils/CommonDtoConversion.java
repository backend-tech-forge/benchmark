package org.benchmarker.bmcontroller.template.service.utils;

import java.time.Duration;
import java.util.stream.Collectors;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcontroller.template.model.TestResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * convert Entity to DTO
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = Exception.class)
public class CommonDtoConversion {

    // TODO : 다량의 정보를 불러오는 만큼 캐싱 필요
    public static CommonTestResult convertToCommonTestResult(TestResult testResult) {
        // check parent transaction or exception
        ParentTXCheck.IsParentTransactionActive();

        return CommonTestResult.builder()
            .groupId(testResult.getTestExecution().getTestTemplate().getUserGroup().getId())
            .testStatus(testResult.getAgentStatus())
            .totalRequests(testResult.getTotalRequest())
            .totalErrors(testResult.getTotalError())
            .totalUsers(testResult.getTestExecution().getTestTemplate().getVuser())
            .totalSuccess(testResult.getTotalSuccess())
            .url(testResult.getTestExecution().getTestTemplate().getUrl())
            .tpsAverage(testResult.getTpsAvg())
            .mttfbAverage(testResult.getMttbfbAvg())
            .startedAt(testResult.getStartedAt().toString())
            .finishedAt(testResult.getFinishedAt().toString())
            .totalDuration(
                Duration.between(testResult.getStartedAt(), testResult.getFinishedAt()).toString())
            .statusCodeCount(testResult.getTestStatuses().stream().collect(
                Collectors.toMap((status) -> status.getCode().toString(),
                    (status) -> status.getCount())))
            // TODO : 현재 mttfb, tps percentile 정보가 저장되어있지 않고 그때그때 연산필요.
            // TODO : TestStatus 가 GET, POST 와 같은 정보를 저장하고 있음. 이는 맞지 않는 스키마이며 HTTP Status Code 를 저장해야함.
            // HTTP Status Code 는 200, 201, 400, 404, 500 등이 있음. 따라서 엔티티 내부 필드에서 int 로 저장해야함.
            .build();
    }
}
