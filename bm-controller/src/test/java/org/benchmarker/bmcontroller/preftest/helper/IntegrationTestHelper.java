package org.benchmarker.bmcontroller.preftest.helper;

import lombok.RequiredArgsConstructor;
import org.benchmarker.bmcontroller.template.helper.TemplateHelper;
import org.benchmarker.bmcontroller.template.helper.TestExecutionHelper;
import org.benchmarker.bmcontroller.template.helper.TestMTTFBHelper;
import org.benchmarker.bmcontroller.template.helper.TestResultHelper;
import org.benchmarker.bmcontroller.template.helper.TestStatusHelper;
import org.benchmarker.bmcontroller.template.helper.TestTpsHelper;
import org.benchmarker.bmcontroller.template.model.TestExecution;
import org.benchmarker.bmcontroller.template.model.TestMttfb;
import org.benchmarker.bmcontroller.template.model.TestResult;
import org.benchmarker.bmcontroller.template.model.TestStatus;
import org.benchmarker.bmcontroller.template.model.TestTemplate;
import org.benchmarker.bmcontroller.template.model.TestTps;
import org.benchmarker.bmcontroller.template.repository.TestExecutionRepository;
import org.benchmarker.bmcontroller.template.repository.TestMttfbRepository;
import org.benchmarker.bmcontroller.template.repository.TestResultRepository;
import org.benchmarker.bmcontroller.template.repository.TestStatusRepository;
import org.benchmarker.bmcontroller.template.repository.TestTemplateRepository;
import org.benchmarker.bmcontroller.template.repository.TestTpsRepository;
import org.benchmarker.bmcontroller.user.helper.UserHelper;
import org.benchmarker.bmcontroller.user.model.User;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.model.UserGroupJoin;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * For Integration Testing Helper
 */
@Component
@ActiveProfiles("test")
@RequiredArgsConstructor
public class IntegrationTestHelper {
    private final TestResultRepository testResultRepository;
    private final TestMttfbRepository testMttfbRepository;
    private final TestStatusRepository testStatusRepository;
    private final TestTpsRepository testTpsRepository;
    private final TestExecutionRepository testExecutionRepository;
    private final TestTemplateRepository testTemplateRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;
    private final UserGroupJoinRepository userGroupJoinRepository;

    /**
     * Save basic data for testing
     * <p>1. User, UserGroup, UserGroupJoin</p>
     * <p>2. TestTemplate</p>
     * <p>3. TestExecution</p>
     * <p>4. TestResult</p>
     * <p>5. TestTps, TestMTTFB, TestStatus</p>
     */
    @Transactional
    public void saveBasics(){
        // save user, userGroup, userGroupJoin
        User defaultUser = UserHelper.createDefaultUser();
        UserGroup defaultUserGroup = UserHelper.createDefaultUserGroup();

        UserGroup saveUserGroup = userGroupRepository.save(defaultUserGroup);
        User saveUser = userRepository.save(defaultUser);

        UserGroupJoin join = UserGroupJoin.builder()
            .user(saveUser)
            .userGroup(saveUserGroup)
            .build();

        UserGroupJoin saveUserGroupJoin = userGroupJoinRepository.save(join);

        // save testTemplate
        TestTemplate defaultTemplate = TemplateHelper.createDefaultTemplate();
        defaultTemplate.setUserGroup(saveUserGroup);
        TestTemplate saveTestTemplate = testTemplateRepository.save(defaultTemplate);

        // save testExecution
        TestExecution defaultTestExecution = TestExecutionHelper.createDefaultTestExecution();
        defaultTestExecution.setTestTemplate(saveTestTemplate);
        TestExecution saveTestExecution = testExecutionRepository.save(defaultTestExecution);

        // save testResult
        TestResult defaultTestResult = TestResultHelper.createDefaultTestResult();
        defaultTestResult.setTestExecution(saveTestExecution);
        TestResult saveTestResult = testResultRepository.save(defaultTestResult);

        // save MTTFB, TPS, Status
        TestTps defaultTestTps = TestTpsHelper.createDefaultTestTps();
        defaultTestTps.setTestResult(saveTestResult);
        testTpsRepository.save(defaultTestTps);

        TestMttfb defaultTestMTTFB = TestMTTFBHelper.createDefaultTestMTTFB();
        defaultTestMTTFB.setTestResult(saveTestResult);
        testMttfbRepository.save(defaultTestMTTFB);

        TestStatus defaultTestStatus = TestStatusHelper.createDefaultTestStatus();
        defaultTestStatus.setTestResult(saveTestResult);
        testStatusRepository.save(defaultTestStatus);

    }

}
