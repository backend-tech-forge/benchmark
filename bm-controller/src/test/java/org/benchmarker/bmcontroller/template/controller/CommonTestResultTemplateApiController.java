package org.benchmarker.bmcontroller.template.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.benchmarker.bmcommon.dto.CommonTestResult;
import org.benchmarker.bmcontroller.template.controller.dto.SaveResultResDto;
import org.benchmarker.bmcontroller.template.model.TestTemplate;
import org.benchmarker.bmcontroller.template.repository.*;
import org.benchmarker.bmcontroller.template.service.TestResultService;
import org.benchmarker.bmcontroller.user.controller.constant.TestUserConsts;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.util.annotations.RestDocsTest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@RestDocsTest
class CommonTestResultTemplateApiController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestResultService testResultService;

    @Autowired
    private TestTemplateRepository testTemplateRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    UserGroupRepository userGroupRepository;

    @MockBean
    UserContext userContext;

    @MockBean
    UserGroupJoinRepository userGroupJoinRepository;

    @Autowired
    private TemplateResultStatusRepository templateResultStatusRepository;

    @Autowired
    private MttfbRepository mttfbRepository;

    @Autowired
    private TpsRepository tpsRepository;

    @AfterEach
    void clear() {
        templateResultStatusRepository.deleteAll();
        tpsRepository.deleteAll();
        mttfbRepository.deleteAll();

        testResultRepository.deleteAll();
        testTemplateRepository.deleteAll();
    }

    @Test
    @DisplayName("agent 결과 저장 생성 하는 테스트")
    @WithMockUser(username = TestUserConsts.id, roles = "USER")
    public void resultSaveAndReturn() throws Exception {

        //given
        TestTemplate testTemplate = getTestTemplate();

        CommonTestResult req = CommonTestResult.builder()
                .testId(testTemplate.getId())
                .startedAt(String.valueOf(LocalDateTime.now()))
                .finishedAt(String.valueOf(LocalDateTime.now().plusSeconds(2)))
                .url(testTemplate.getUrl())
                .method(testTemplate.getMethod())
                .statusCode(200)
                .totalRequests(5)
                .totalSuccess(3)
                .totalErrors(2)
                .totalUsers(5)
                .mttfbAverage(3.0)
                .tpsAverage(0.5)
                .build();

        SaveResultResDto res = SaveResultResDto.builder()
                .testId(testTemplate.getId())
                .startedAt(LocalDateTime.parse(req.getStartedAt()))
                .finishedAt(LocalDateTime.parse(req.getFinishedAt()))
                .url(req.getUrl())
                .method(req.getMethod())
                .totalRequest(req.getTotalRequests())
                .totalSuccess(req.getTotalSuccess())
                .totalError(req.getTotalErrors())
                .tpsAvg(req.getTpsAverage())
                .mttbfbAvg(req.getMttfbAverage())
                .build();

        // when
        when(testResultService.resultSaveAndReturn(any())).thenReturn(Optional.ofNullable(res));

        // then
        mockMvc.perform(post("/api/testResult")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andDo(result -> {
                    assertThat(result.getResponse().getStatus()).isEqualTo(200);

                    SaveResultResDto resTemplate = objectMapper.readValue(
                            result.getResponse().getContentAsString(StandardCharsets.UTF_8),
                            SaveResultResDto.class);

                    assertThat(resTemplate.getTestId()).isEqualTo(testTemplate.getId());
                    assertThat(resTemplate.getStartedAt()).isEqualTo(req.getStartedAt());
                    assertThat(resTemplate.getFinishedAt()).isEqualTo(req.getFinishedAt());
                    assertThat(resTemplate.getTotalRequest()).isEqualTo(req.getTotalRequests());
                    assertThat(resTemplate.getTotalSuccess()).isEqualTo(req.getTotalSuccess());
                    assertThat(resTemplate.getTotalError()).isEqualTo(req.getTotalErrors());
                    assertThat(resTemplate.getTpsAvg()).isEqualTo(req.getTpsAverage());
                    assertThat(resTemplate.getMttbfbAvg()).isEqualTo(req.getMttfbAverage());
                });
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

}