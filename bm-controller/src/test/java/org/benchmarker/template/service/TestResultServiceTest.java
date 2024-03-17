package org.benchmarker.template.service;

import org.benchmarker.security.BMUserDetails;
import org.benchmarker.template.controller.dto.TestResultResponseDto;
import org.benchmarker.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.template.model.TemplateResult;
import org.benchmarker.template.repository.TestResultRepository;
import org.benchmarker.template.repository.TestTemplateRepository;
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

import java.util.List;
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
    private UserRepository userRepository;

    @Autowired
    private TestTemplateService testTemplateService;

    @Autowired
    private TestResultService testResultService;

    @MockBean
    private UserContext userContext;

    @AfterEach
    public void clear() {
//        userRepository.deleteAll();
//        userGroupRepository.deleteAll();
        testResultRepository.deleteAll();
        testTemplateRepository.deleteAll();
    }

    @Test
    @DisplayName("성능 측적 method 호출 시 결과 저장 후 반환 확인하는 테스트")
    public void createTemplateResult() throws InterruptedException {

        //given
        Optional<TestTemplateResponseDto> template = saveTempData();

        //when
        TestResultResponseDto testResultResponseDto = testResultService.measurePerformance(template.get().getId());

        //then
//        assertThat(testResultResponseDto.size()).isEqualTo(3);

    }

    public Optional<TestTemplateResponseDto> saveTempData() {

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

}