package org.benchmarker.bmcontroller.template.service;

import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.template.controller.dto.ResultResDto;
import org.benchmarker.bmcontroller.template.controller.dto.SaveResultReqDto;
import org.benchmarker.bmcontroller.template.controller.dto.SaveResultResDto;
import org.benchmarker.bmcontroller.template.model.*;
import org.benchmarker.bmcontroller.template.repository.*;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.util.initialize.MockServer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("결과 처리 관련 테스트")
class TestResultServiceTest extends MockServer {

    @Autowired
    private TestTemplateRepository testTemplateRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private MttfbRepository mttfbRepository;

    @Autowired
    private TpsRepository tpsRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private TemplateResultStatusRepository templateResultStatusRepository;

    @Autowired
    private TestResultService testResultService;

    @AfterEach
    public void clear() {
        templateResultStatusRepository.deleteAll();
        tpsRepository.deleteAll();
        mttfbRepository.deleteAll();

        testResultRepository.deleteAll();
        testTemplateRepository.deleteAll();
    }

    @Test
    @DisplayName("agent 결과 받아서 저장하는 테스트")
    public void saveResultAndReturnTest() {

        // given
        TestTemplate testTemplate = getTestTemplate();

        SaveResultReqDto req = SaveResultReqDto.builder()
                .testId(testTemplate.getId())
                .startedAt(LocalDateTime.now())
                .finishedAt(LocalDateTime.now().plusSeconds(2))
                .url(testTemplate.getUrl())
                .method(testTemplate.getMethod())
                .statusCode(200)
                .totalRequest(5)
                .totalSuccess(3)
                .totalError(2)
                .totalUsers(5)
                .mttbfbAvg(3.0)
                .tpsAvg(0.5)
                .build();

        // when
        SaveResultResDto saveResultResDto = testResultService.resultSaveAndReturn(req)
                .orElseThrow(() -> new GlobalException(ErrorCode.BAD_REQUEST));

        TestResult saveResult = testResultRepository.findById(saveResultResDto.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.BAD_REQUEST));

        List<Mttfb> mttfbs = mttfbRepository.findByTestResult(saveResult);
        List<Tps> tps = tpsRepository.findByTestResult(saveResult);
        List<TemplateResultStatus> statuses = templateResultStatusRepository.findByTestResult(saveResult);

        // then
        assertThat(saveResult.getTestTemplate()).isEqualTo(testTemplate);
        assertThat(saveResult.getStartedAt()).isEqualTo(req.getStartedAt());
        assertThat(saveResult.getFinishedAt()).isEqualTo(req.getFinishedAt());
        assertThat(saveResult.getTotalRequest()).isEqualTo(req.getTotalRequest());
        assertThat(saveResult.getTotalSuccess()).isEqualTo(req.getTotalSuccess());
        assertThat(saveResult.getTotalError()).isEqualTo(req.getTotalError());
        assertThat(saveResult.getTpsAvg()).isEqualTo(req.getTpsAvg());
        assertThat(saveResult.getMttbfbAvg()).isEqualTo(req.getMttbfbAvg());

        assertThat(mttfbs.get(0).getTestResult()).isEqualTo(saveResult);
        assertThat(tps.get(0).getTestResult()).isEqualTo(saveResult);
        assertThat(statuses.get(0).getTestResult()).isEqualTo(saveResult);

    }

    @Test
    @DisplayName("agent 결과 받아서 client 에 보여주는 테스트")
    public void getTemplateResult() {

        // given
        TestTemplate testTemplate = getTestTemplate();
        List<SaveResultResDto> results = saveResults(testTemplate);

        // when
        ResultResDto templateResult = testResultService.getTemplateResult(testTemplate.getId());

        // then
        assertThat(templateResult.getTestId()).isEqualTo(testTemplate.getId());
        assertThat(templateResult.getStartedAt()).isEqualTo(String.valueOf(results.get(0).getStartedAt()));
        assertThat(templateResult.getFinishedAt()).isEqualTo(String.valueOf(results.get(results.size() - 1).getFinishedAt()));
        assertThat(templateResult.getTotalRequest()).isEqualTo(5);
        assertThat(templateResult.getTotalSuccess()).isEqualTo(5);
        assertThat(templateResult.getTotalError()).isEqualTo(0);

        assertThat(templateResult.getStatusCodeCount().size()).isEqualTo(1);
        assertThat(templateResult.getTpsPercentiles().size()).isEqualTo(4);
        assertThat(templateResult.getMttfbPercentiles().size()).isEqualTo(4);
    }

    private TestTemplate getTestTemplate() {

        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        UserGroup tempGroup = userGroupRepository.save(userGroup);

        TestTemplate request = TestTemplate.builder()
                .url("test.com")
                .method("get")
                .body("")
                .userGroup(tempGroup)
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();

        return testTemplateRepository.save(request);
    }

    private List<SaveResultResDto> saveResults(TestTemplate template) {

        List<SaveResultResDto> testResults = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            SaveResultReqDto req = SaveResultReqDto.builder()
                    .testId(template.getId())
                    .startedAt(LocalDateTime.now())
                    .finishedAt(LocalDateTime.now().plusSeconds(2))
                    .url(template.getUrl())
                    .method(template.getMethod())
                    .statusCode(200)
                    .totalRequest(i + 1)
                    .totalSuccess(1)
                    .totalError(0)
                    .totalUsers(5)
                    .mttbfbAvg(3.0 + i)
                    .tpsAvg(0.5 + i)
                    .build();

            SaveResultResDto tempSaveResult = testResultService.resultSaveAndReturn(req)
                    .orElseThrow(() -> new GlobalException(ErrorCode.BAD_REQUEST));

            testResults.add(tempSaveResult);
        }

        return testResults;
    }

}