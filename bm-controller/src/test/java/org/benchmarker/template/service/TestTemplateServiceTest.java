package org.benchmarker.template.service;

import org.benchmarker.common.error.GlobalException;
import org.benchmarker.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.template.controller.dto.TestTemplateUpdateDto;
import org.benchmarker.template.model.TestTemplate;
import org.benchmarker.template.repository.TestTemplateRepository;
import org.benchmarker.user.model.UserGroup;
import org.benchmarker.user.repository.UserGroupRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("템플릿 관련 테스트")
class TestTemplateServiceTest {


    @Autowired
    private TestTemplateRepository testTemplateRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private TestTemplateService testTemplateService;

    @AfterEach
    void removeAll() {
        testTemplateRepository.deleteAll();
    }

    @Test
    @DisplayName("템플릿 생성 테스트")
    public void createTemplate() {

        //given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        userGroupRepository.save(userGroup);

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                .url("test.com")
                .method("get")
                .body("")
                .userGroupName("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();

        //when
        Optional<TestTemplate> template = testTemplateService.createTemplate(request);

        //then
        assertThat(template).isNotEmpty();
        TestTemplate testTemplate = template.get();
        assertThat(testTemplate).isNotNull();

        assertThat(testTemplate.getUrl()).isEqualTo(request.getUrl());
        assertThat(testTemplate.getMethod()).isEqualTo(request.getMethod());
        assertThat(testTemplate.getBody()).isEqualTo(request.getBody());
        assertThat(testTemplate.getUserGroup()).isEqualTo(userGroup);
        assertThat(testTemplate.getVuser()).isEqualTo(request.getVuser());
        assertThat(testTemplate.getCpuLimit()).isEqualTo(request.getCpuLimit());
        assertThat(testTemplate.getMaxRequest()).isEqualTo(request.getMaxRequest());
        assertThat(testTemplate.getMaxDuration()).isEqualTo(request.getMaxDuration());

    }

    @Test
    @DisplayName("템플릿 생성시 해당되는 유저그룹 없으면 에러를 발생한다")
    public void createUserGroupException() {

        //given
        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                .url("test.com")
                .method("get")
                .body("")
                .userGroupName("userGroup2")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();

        // When & Then
        assertThrows(GlobalException.class, () -> {
            testTemplateService.createTemplate(request);
        });

    }

    @Test
    @DisplayName("템플릿을 업데이트 한다.")
    public void updateTestTemplate() throws Exception {
        //given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        UserGroup tempGroup = userGroupRepository.save(userGroup);

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                .url("test.com")
                .method("get")
                .body("")
                .userGroupName("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();
        TestTemplate template = testTemplateService.createTemplate(request).get();

        TestTemplateUpdateDto updateRequest = TestTemplateUpdateDto.builder()
                .id(template.getId())
                .url("update.com")
                .method("post")
                .body("data=sample")
                .userGroupName("userGroup")
                .vuser(4)
                .cpuLimit(4)
                .maxRequest(4)
                .maxDuration(4)
                .build();

        //when
        TestTemplate updateTestTemplate = testTemplateService.updateTemplate(updateRequest).get();

        //then
        assertThat(updateTestTemplate).isNotNull();

        assertThat(updateTestTemplate.getUrl()).isEqualTo(updateRequest.getUrl());
        assertThat(updateTestTemplate.getMethod()).isEqualTo(updateRequest.getMethod());
        assertThat(updateTestTemplate.getBody()).isEqualTo(updateRequest.getBody());
        assertThat(updateTestTemplate.getUserGroup()).isEqualTo(tempGroup);
        assertThat(updateTestTemplate.getVuser()).isEqualTo(updateRequest.getVuser());
        assertThat(updateTestTemplate.getCpuLimit()).isEqualTo(updateRequest.getCpuLimit());
        assertThat(updateTestTemplate.getMaxRequest()).isEqualTo(updateRequest.getMaxRequest());
        assertThat(updateTestTemplate.getMaxDuration()).isEqualTo(updateRequest.getMaxDuration());
    }

    @Test
    @DisplayName("템플릿을 업데이트시 존재하지 않는 템플릿이라면 에러 발생한다.")
    public void updateTemplateNotFoundException() throws Exception {
        //given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                .url("test.com")
                .method("get")
                .body("")
                .userGroupName("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();
        TestTemplate template = testTemplateService.createTemplate(request).get();

        TestTemplateUpdateDto updateRequest = TestTemplateUpdateDto.builder()
                .id(template.getId() + 1)
                .url("update.com")
                .method("post")
                .body("data=sample")
                .userGroupName("userGroup")
                .vuser(4)
                .cpuLimit(4)
                .maxRequest(4)
                .maxDuration(4)
                .build();

        // When & Then
        assertThrows(GlobalException.class, () -> {
            testTemplateService.updateTemplate(updateRequest);
        });
    }

    @Test
    @DisplayName("템플릿을 삭제 한다.")
    public void deleteTestTemplate() throws Exception {
        //given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                .url("test.com")
                .method("get")
                .body("")
                .userGroupName("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();
        TestTemplate template = testTemplateService.createTemplate(request).get();

        //when
        testTemplateService.deleteTemplate(template.getId());

        //then
        assertThat(testTemplateRepository.findById(template.getId())).isEmpty();
    }

    @Test
    @DisplayName("템플릿을 삭제시 존재하지 않는 템플릿이라면 에러 발생한다.")
    public void deleteTemplateNotFoundException() throws Exception {
        //given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                .url("test.com")
                .method("get")
                .body("")
                .userGroupName("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();
        TestTemplate template = testTemplateService.createTemplate(request).get();

        // When & Then
        assertThrows(GlobalException.class, () -> {
            testTemplateService.deleteTemplate(template.getId() + 1);
        });
    }

}