package org.benchmarker.bmcontroller.template.service;

import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcommon.util.RandomUtils;
import org.benchmarker.bmcontroller.MockServer;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.template.helper.TemplateHelper;
import org.benchmarker.bmcontroller.template.model.TestMttfb;
import org.benchmarker.bmcontroller.template.model.TestResult;
import org.benchmarker.bmcontroller.template.model.TestTemplate;
import org.benchmarker.bmcontroller.template.model.TestTps;
import org.benchmarker.bmcontroller.template.repository.TestMttfbRepository;
import org.benchmarker.bmcontroller.template.repository.TestResultRepository;
import org.benchmarker.bmcontroller.template.repository.TestTemplateRepository;
import org.benchmarker.bmcontroller.template.repository.TestTpsRepository;
import org.benchmarker.bmcontroller.user.helper.UserHelper;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestResultServiceTest extends MockServer {

    @Autowired
    private TestTemplateRepository testTemplateRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private TestMttfbRepository mttfbRepository;

    @Autowired
    private TestTpsRepository tpsRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserGroupJoinRepository userGroupJoinRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestResultService testResultService;

    @AfterEach
    public void clear() {
        tpsRepository.deleteAll();
        mttfbRepository.deleteAll();

        testResultRepository.deleteAll();
        testTemplateRepository.deleteAll();

        userGroupJoinRepository.deleteAll();
        userRepository.deleteAll();
        userGroupRepository.deleteAll();
    }

    @Test
    @DisplayName("agent 결과 받아서 저장하는 테스트")
    public void saveResultAndReturnTest() {

        // given
        User defaultUser = UserHelper.createDefaultUser();
        userRepository.save(defaultUser);

        UserGroup userGroup = UserHelper.createDefaultUserGroup();
        userGroupRepository.save(userGroup);

        TestTemplate testTemplate = TemplateHelper.createDefaultTemplate();
        testTemplate.setUserGroup(userGroup);
        TestTemplate saveTemplate = testTemplateRepository.save(testTemplate);

        CommonTestResult req = RandomUtils.generateRandomTestResult();
        req.setTestId(saveTemplate.getId());

        // when
        CommonTestResult saveResultResDto = testResultService.resultSaveAndReturn(req)
                .orElseThrow(() -> new GlobalException(ErrorCode.BAD_REQUEST));

        TestResult saveResult = testResultRepository.findById(saveResultResDto.getTestId())
                .orElseThrow(() -> new GlobalException(ErrorCode.BAD_REQUEST));

        List<TestMttfb> mttfbs = mttfbRepository.findByTestResult(saveResult);
        List<TestTps> tps = tpsRepository.findByTestResult(saveResult);

        // then
        assertThat(saveResult.getTestTemplate()).isEqualTo(testTemplate);
        assertThat(saveResult.getTotalRequest()).isEqualTo(req.getTotalRequests());
        assertThat(saveResult.getTotalSuccess()).isEqualTo(req.getTotalSuccess());
        assertThat(saveResult.getTotalError()).isEqualTo(req.getTotalErrors());
        assertThat(saveResult.getTpsAvg()).isEqualTo(req.getTpsAverage());
        assertThat(saveResult.getMttbfbAvg()).isEqualTo(req.getMttfbAverage());

        assertThat(mttfbs.get(0).getTestResult()).isEqualTo(saveResult);
        assertThat(tps.get(0).getTestResult()).isEqualTo(saveResult);

    }

    @Test
    @DisplayName("agent 결과 받아서 저장 할 때 존재 하지 않는 템플릿 정보를 받았을 때 에러 테스트")
    public void saveResultNotTemplateException() {

        // given
        User defaultUser = UserHelper.createDefaultUser();
        userRepository.save(defaultUser);

        UserGroup userGroup = UserHelper.createDefaultUserGroup();
        userGroupRepository.save(userGroup);

        TestTemplate testTemplate = TemplateHelper.createDefaultTemplate();
        testTemplate.setUserGroup(userGroup);

        CommonTestResult req = RandomUtils.generateRandomTestResult();
        req.setTestId(9999);

        // When & Then
        assertThrows(GlobalException.class, () -> {
            testResultService.resultSaveAndReturn(req);
        });

    }

}