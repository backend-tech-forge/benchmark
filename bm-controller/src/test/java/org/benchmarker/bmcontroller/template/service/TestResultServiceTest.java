package org.benchmarker.bmcontroller.template.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.benchmarker.bmcontroller.common.beans.RequestCounter;
import org.benchmarker.bmcontroller.template.controller.dto.TestResultResponseDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.bmcontroller.template.repository.TemplateResultErrorLogRepository;
import org.benchmarker.bmcontroller.template.repository.MttfbRepository;
import org.benchmarker.bmcontroller.template.repository.TestResultRepository;
import org.benchmarker.bmcontroller.template.repository.TestTemplateRepository;
import org.benchmarker.bmcontroller.template.repository.TpsRepository;
import org.benchmarker.bmcontroller.user.model.UserGroup;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.benchmarker.bmcontroller.user.repository.UserRepository;
import org.benchmarker.bmcontroller.user.service.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.util.initialize.MockServer;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("webClinet 관련 테스트")
class TestResultServiceTest extends MockServer {

    @Autowired
    private TestTemplateRepository testTemplateRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private MttfbRepository mttfbRepository;

    @Autowired
    private TpsRepository tpsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestTemplateService testTemplateService;
    @Autowired
    private TemplateResultErrorLogRepository templateResultErrorLogRepository;
    @MockBean
    private UserContext userContext;
    @Autowired
    private ObjectMapper objectMapper;
    private ITestResultService testResultService;

    @BeforeEach
    public void setUpEach() {
        WebClient webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();

        testResultService = new TestResultService(testTemplateRepository, testResultRepository,
            tpsRepository, mttfbRepository, templateResultErrorLogRepository, userGroupRepository,
            webClient, new RequestCounter());
    }

    @AfterEach
    public void clear() {
        mttfbRepository.deleteAll();
        tpsRepository.deleteAll();
        testResultRepository.deleteAll();
        testTemplateRepository.deleteAll();
    }

    @Test
    @DisplayName("성능 측적 method 호출 시 결과 저장 후 반환 확인하는 테스트")
    public void getMethodTemplateResultTest() throws InterruptedException {

        //given
        Optional<TestTemplateResponseDto> template = saveGetTempData(
            mockBackEnd.url("/").toString());
        // mockBackend 에 응답할 오브젝트를 큐에 9번 반복하여 추가합니다
        addMockResponse("test-response-1", 9);

        //when
        TestResultResponseDto testResultResponseDto = testResultService.measurePerformance(
            "userGroup", template.get().getId(), "start");

        //then
        assertThat(testResultResponseDto.getMethod()).isEqualTo(template.get().getMethod());
        assertThat(testResultResponseDto.getUrl()).isEqualTo(template.get().getUrl());
        assertThat(testResultResponseDto.getTotalUsers()).isEqualTo(template.get().getVuser());
    }

    @Test
    @DisplayName("성능 측적 method 호출 시 결과 저장 후 반환 확인하는 테스트")
    public void postMethodTemplateResultTest()
        throws InterruptedException {

        //given
        Optional<TestTemplateResponseDto> template = saveGetTempData(
            mockBackEnd.url("/").toString());
        // mockBackend 에 응답할 오브젝트를 큐에 9번 반복하여 추가합니다
        addMockResponse("test-response-1", 9);

        //when
        TestResultResponseDto testResultResponseDto = testResultService.measurePerformance(
            "userGroup", template.get().getId(), "start");

        //then
        assertThat(testResultResponseDto.getMethod()).isEqualTo(template.get().getMethod());
        assertThat(testResultResponseDto.getUrl()).isEqualTo(template.get().getUrl());
        assertThat(testResultResponseDto.getTotalUsers()).isEqualTo(template.get().getVuser());

    }

    public Optional<TestTemplateResponseDto> saveGetTempData() {

        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        userGroupRepository.save(userGroup);

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
            .url("http://localhost:8080/login")
            .method("get")
            .body("")
            .userGroupId("userGroup")
            .vuser(3)
            .cpuLimit(3)
            .maxRequest(3)
            .maxDuration(3)
            .build();

        return testTemplateService.createTemplate(request);
    }

    public Optional<TestTemplateResponseDto> saveGetTempData(String url) {

        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        userGroupRepository.save(userGroup);

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
            .url(url)
            .method("get")
            .body("")
            .userGroupId("userGroup")
            .vuser(3)
            .cpuLimit(3)
            .maxRequest(3)
            .maxDuration(3)
            .build();

        return testTemplateService.createTemplate(request);
    }

    public Optional<TestTemplateResponseDto> savePostTempData() throws JsonProcessingException {

        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        userGroupRepository.save(userGroup);

        /**
         * "userGroupName" : "default",
         *     "url" : "test.com",
         *     "method" : "get",
         *     "body" : "",
         *     "vuser" : "3",
         *     "maxRequest" : 4,
         *     "maxDuration" : 5,
         *     "cpuLimit" : 3
         */
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("userGroupName", "default");
        bodyMap.put("url", "test.com");
        bodyMap.put("method", "get");
        bodyMap.put("body", "");
        bodyMap.put("vuser", 3);
        bodyMap.put("maxRequest", 3);
        bodyMap.put("maxDuration", 3);
        bodyMap.put("cpuLimit", 3);

        String jsonBody = objectMapper.writeValueAsString(bodyMap);

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
            .url("http://localhost:8080/api/template")
            .method("post")
            .body(jsonBody)
            .userGroupId("userGroup")
            .vuser(3)
            .cpuLimit(3)
            .maxRequest(3)
            .maxDuration(3)
            .build();

        return testTemplateService.createTemplate(request);
    }

}