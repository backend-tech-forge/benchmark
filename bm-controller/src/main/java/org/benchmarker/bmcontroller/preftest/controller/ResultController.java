package org.benchmarker.bmcontroller.preftest.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.dto.MTTFBInfo;
import org.benchmarker.bmcommon.dto.TPSInfo;
import org.benchmarker.bmcontroller.common.controller.annotation.GlobalControllerModel;
import org.benchmarker.bmcontroller.common.model.BaseTime;
import org.benchmarker.bmcontroller.template.model.TestExecution;
import org.benchmarker.bmcontroller.template.model.TestMttfb;
import org.benchmarker.bmcontroller.template.model.TestResult;
import org.benchmarker.bmcontroller.template.model.TestTps;
import org.benchmarker.bmcontroller.template.service.TestExecutionService;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.benchmarker.bmcontroller.user.util.UserServiceUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Slf4j
@RequiredArgsConstructor
@GlobalControllerModel
public class ResultController {

    private final TestExecutionService testExecutionService;
    private final UserServiceUtils userServiceUtils;
    private final UserContext userContext;


    @GetMapping("/templates/{template_id}/tests/{test_id}")
    @PreAuthorize("hasAnyRole('USER')")
    @Transactional(readOnly = true)
    public String showChart(
        @PathVariable("template_id") Integer templateId,
        @PathVariable("test_id") String testId,
        Model model
    ) {
        // verify user access to template
        User currentUser = userContext.getCurrentUser();
        userServiceUtils.verifyAccessTemplate(currentUser.getId(), templateId);

        // get results
        TestExecution test = testExecutionService.getTest(testId);
        List<TestResult> testResults = test.getTestResults();
        testResults.sort(Comparator.comparing(BaseTime::getCreatedAt));

        // time series data
        List<MTTFBInfo> mttfbInfoList = new ArrayList<>();
        List<TPSInfo> tpsInfoList = new ArrayList<>();

        AtomicReference<TestResult> resultSets = new AtomicReference<TestResult>();

        // TODO : N+1 prob. n.n
        testResults.stream().forEach(testResult -> {
            TestMttfb testMttfb = testResult.getTestMttfbs().get(0);
            TestTps testTps = testResult.getTestTps().get(0);
            mttfbInfoList.add(
                MTTFBInfo.builder().timestamp(testMttfb.getCreatedAt()).mttbfb(testMttfb.getMttfb())
                    .build());
            tpsInfoList.add(
                TPSInfo.builder().timestamp(testTps.getCreatedAt()).tps(testTps.getTransaction())
                    .build());
            assert false;
            resultSets.set(testResult);
        });

        CommonTestResult lastTestResult = testExecutionService.getLastTestResult(testId);

        model.addAttribute("commonTestResult",lastTestResult);
        model.addAttribute("mttfbInfoList", mttfbInfoList);
        model.addAttribute("tpsInfoList", tpsInfoList);

        return "template/chart";
    }

}