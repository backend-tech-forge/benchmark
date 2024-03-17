package org.benchmarker.template.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.benchmarker.security.BMUserDetails;
import org.benchmarker.template.controller.dto.TestResultResponseDto;
import org.benchmarker.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.template.model.TemplateResult;
import org.benchmarker.template.repository.TestMttfbRepository;
import org.benchmarker.template.repository.TestResultRepository;
import org.benchmarker.template.repository.TestTemplateRepository;
import org.benchmarker.template.repository.TestTpsRepository;
import org.benchmarker.user.model.User;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.repository.UserGroupRepository;
import org.benchmarker.user.repository.UserRepository;
import org.benchmarker.user.service.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("webClinet 관련 테스트")
class TestResultServiceTest {

    @Autowired
    private TestTemplateRepository testTemplateRepository;

    @Autowired
    private TestResultRepository testResultRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private TestMttfbRepository mttfbRepository;

    @Autowired
    private TestTpsRepository tpsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestTemplateService testTemplateService;

    @Autowired
    private TestResultService testResultService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void clear() {
//        userRepository.deleteAll();
//        userGroupRepository.deleteAll();
        mttfbRepository.deleteAll();
        tpsRepository.deleteAll();
        testResultRepository.deleteAll();
        testTemplateRepository.deleteAll();
    }

    @Test
    @DisplayName("성능 측적 method 호출 시 결과 저장 후 반환 확인하는 테스트")
    public void getMethodTemplateResultTest() throws InterruptedException {

        //given
        Optional<TestTemplateResponseDto> template = saveGetTempData();

        //when
        TestResultResponseDto testResultResponseDto = testResultService.measurePerformance(template.get().getId());

        //then
        System.out.println("확인");
        assertThat(testResultResponseDto.getMethod()).isEqualTo(template.get().getMethod());
        assertThat(testResultResponseDto.getUrl()).isEqualTo(template.get().getUrl());
        assertThat(testResultResponseDto.getTotalUsers()).isEqualTo(template.get().getVuser());

    }

    @Test
    @DisplayName("성능 측적 method 호출 시 결과 저장 후 반환 확인하는 테스트")
    public void postMethodTemplateResultTest() throws InterruptedException, JsonProcessingException {

        //given
        Optional<TestTemplateResponseDto> template = savePostTempData();

        //when
        TestResultResponseDto testResultResponseDto = testResultService.measurePerformance(template.get().getId());

        //then
        System.out.println("확인");
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