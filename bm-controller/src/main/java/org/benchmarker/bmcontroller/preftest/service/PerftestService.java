package org.benchmarker.bmcontroller.preftest.service;

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.bmcontroller.preftest.common.TestInfo;
import org.benchmarker.bmcontroller.preftest.model.RunningTest;
import org.benchmarker.bmcontroller.preftest.repository.RunningTestRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@Getter
@Slf4j
@RequiredArgsConstructor
public class PerftestService {

    // KEY: groupId, VALUE: TestInfo
//    private ConcurrentHashMap<String, Set<TestInfo>> runningTemplates = new ConcurrentHashMap<>();
    private final RunningTestRepository runningTestRepository;

    /**
     * {@link TestInfo} 를 현재 실행중인 리스트에 저장합니다.
     *
     * @param testInfo
     */
    public void saveRunning(TestInfo testInfo) {
        runningTestRepository.save(RunningTest.builder().testId(testInfo.getTestId()).templateId(
            testInfo.getTemplateId()).groupId(testInfo.getGroupId()).build());
//        Set<TestInfo> templates = runningTemplates.get(testInfo.getGroupId());
//        if (templates != null) {
//            templates.add(testInfo);
//        } else {
//            HashSet<TestInfo> temp = new HashSet<TestInfo>();
//            temp.add(testInfo);
//            runningTemplates.put(testInfo.getGroupId(), temp);
//        }
//        log.info(runningTemplates.toString());
    }


    /**
     * {@link TestInfo} 의 templateId 와 매칭되는 Set 을 제거합니다.
     * @param testInfo {@link TestInfo}
     */
    public void removeRunning(TestInfo testInfo) {
        log.info("delete testInfo with :{}",testInfo.toString());
        Optional<RunningTest> findRunning = runningTestRepository.findById(testInfo.getTemplateId());
        if (findRunning.isEmpty()){
            return;
        }
        runningTestRepository.delete(findRunning.get());
//        Set<TestInfo> templates = runningTemplates.get(testInfo.getGroupId());
//
//        if (templates == null || templates.isEmpty()) {
//            return;
//        }
//
//        Iterator<TestInfo> iterator = templates.iterator();
//        while (iterator.hasNext()) {
//            TestInfo template = iterator.next();
//            if (template.getTemplateId().equals(testInfo.getTemplateId())) {
//                iterator.remove();
//                break; // 일치하는 것을 찾았으면 더 이상 반복하지 않고 종료
//            }
//        }
//
//        // 제거된 템플릿 세트가 비어있으면 맵에서 해당 groupId를 제거
//        if (templates.isEmpty()) {
//            runningTemplates.remove(testInfo.getGroupId());
//        }
    }

    /**
     * Check if template related test is running
     *
     * @param testInfo
     * @return null if no matching templateId is running, or String testId if matching templateId found
     */
    public String isRunning(TestInfo testInfo) {
        Optional<RunningTest> findRunning = runningTestRepository.findById(testInfo.getTemplateId());
        return findRunning.map(RunningTest::getTestId).orElse(null);
        //        Set<TestInfo> templates = runningTemplates.get(testInfo.getGroupId());
//        // 주어진 testInfo의 groupId에 해당하는 템플릿 set 없거나 비어있으면 실행 중인 것으로 간주하지 않음
//        if (templates == null || templates.isEmpty()) {
//            return null;
//        }
//        // 템플릿 세트에 주어진 testInfo의 templateId와 동일한 것이 있는지 확인
//        for (TestInfo template : templates) {
//            if (template.getTemplateId().equals(testInfo.getTemplateId())) {
//                return template.getTestId(); // 동일한 templateId를 가진 것이 있으면 실행 중으로 간주
//            }
//        }
//        return null; // 동일한 templateId를 가진 것이 없으면 실행 중이 아님
    }

    /**
     * Execute a performance test request to the bm-agent API and receive intermediate results via
     * Server-Sent Events (SSE).
     *
     * @param templateId
     * @param action
     * @param webClient
     * @param templateInfo
     * @return Flux {@link ServerSentEvent} {@link CommonTestResult}
     */
    public Flux<ServerSentEvent<CommonTestResult>> executePerformanceTest(Integer templateId,
        String groupId,
        String action, WebClient webClient, TemplateInfo templateInfo) {
        ParameterizedTypeReference<ServerSentEvent<CommonTestResult>> typeReference =
            new ParameterizedTypeReference<ServerSentEvent<CommonTestResult>>() {
            };
        return webClient.post()
            .uri("/api/groups/{groupId}/templates/{templateId}?action={action}", groupId,
                templateId, action)
            .bodyValue(templateInfo)
            .retrieve()
            .bodyToFlux(typeReference)
            .log(); // TODO : remove
    }
}
