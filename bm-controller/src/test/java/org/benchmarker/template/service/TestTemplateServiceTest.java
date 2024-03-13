package org.benchmarker.template.service;

import org.benchmarker.common.error.GlobalException;
import org.benchmarker.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.template.controller.dto.TestTemplateResponseDto;
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

import java.util.ArrayList;
import java.util.List;
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
                .userGroupId("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();

        //when
        Optional<TestTemplateResponseDto> template = testTemplateService.createTemplate(request);

        //then
        assertThat(template).isNotEmpty();
        TestTemplateResponseDto testTemplate = template.get();
        assertThat(testTemplate).isNotNull();

        assertThat(testTemplate.getUrl()).isEqualTo(request.getUrl());
        assertThat(testTemplate.getMethod()).isEqualTo(request.getMethod());
        assertThat(testTemplate.getBody()).isEqualTo(request.getBody());
        assertThat(testTemplate.getUserGroupId()).isEqualTo(userGroup.getName());
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
                .userGroupId("userGroup2")
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
    @DisplayName("존재하는 템플릿을 검색한다")
    public void getTemplate() {
        //given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        UserGroup tempGroup = userGroupRepository.save(userGroup);

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                .url("test.com")
                .method("get")
                .body("")
                .userGroupId("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();
        TestTemplateResponseDto template = testTemplateService.createTemplate(request).get();

        //when
        TestTemplateResponseDto schTemplate = testTemplateService.getTemplate(template.getId());

        //then
        assertThat(schTemplate.getUrl()).isEqualTo(request.getUrl());
        assertThat(schTemplate.getMethod()).isEqualTo(request.getMethod());
        assertThat(schTemplate.getBody()).isEqualTo(request.getBody());
        assertThat(schTemplate.getVuser()).isEqualTo(request.getVuser());
        assertThat(schTemplate.getCpuLimit()).isEqualTo(request.getCpuLimit());
        assertThat(schTemplate.getMaxRequest()).isEqualTo(request.getMaxRequest());
        assertThat(schTemplate.getMaxDuration()).isEqualTo(request.getMaxDuration());
    }

    @Test
    @DisplayName("존재하지 않는 템플릿 조회시 에러가 발생한다.")
    public void getNotFoundTemplateException() {
        //given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        UserGroup tempGroup = userGroupRepository.save(userGroup);

        TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                .url("test.com")
                .method("get")
                .body("")
                .userGroupId("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();
        TestTemplateResponseDto template = testTemplateService.createTemplate(request).get();

        // When & Then
        assertThrows(GlobalException.class, () -> {
            testTemplateService.getTemplate(template.getId() + 1);
        });
    }

    @Test
    @DisplayName("템플릿 목록을 조회 한다.")
    public void getTemplates() {

        //given
        UserGroup userGroup = UserGroup.builder().id("userGroup").name("userGroup").build();
        UserGroup tempGroup = userGroupRepository.save(userGroup);

        List<TestTemplate> testTemplates = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {

            TestTemplateRequestDto request = TestTemplateRequestDto.builder()
                    .url("test.com" + i)
                    .method("get")
                    .body("")
                    .userGroupId("userGroup")
                    .vuser(i)
                    .cpuLimit(i)
                    .maxRequest(i)
                    .maxDuration(i)
                    .build();

            testTemplates.add(request.toEntity());
        }
        testTemplateRepository.saveAll(testTemplates);

        //when
        List<TestTemplateResponseDto> templates = testTemplateService.getTemplates();

        //then
        assertThat(templates.size()).isEqualTo(5);
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
                .userGroupId("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();
        TestTemplateResponseDto template = testTemplateService.createTemplate(request).get();

        TestTemplateUpdateDto updateRequest = TestTemplateUpdateDto.builder()
                .id(template.getId())
                .url("update.com")
                .method("post")
                .body("data=sample")
                .userGroupId("userGroup")
                .vuser(4)
                .cpuLimit(4)
                .maxRequest(4)
                .maxDuration(4)
                .build();

        //when
        TestTemplateResponseDto updateTestTemplate = testTemplateService.updateTemplate(updateRequest).get();

        //then
        assertThat(updateTestTemplate).isNotNull();

        assertThat(updateTestTemplate.getUrl()).isEqualTo(updateRequest.getUrl());
        assertThat(updateTestTemplate.getMethod()).isEqualTo(updateRequest.getMethod());
        assertThat(updateTestTemplate.getBody()).isEqualTo(updateRequest.getBody());
        assertThat(updateTestTemplate.getUserGroupId()).isEqualTo(tempGroup.getName());
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
                .userGroupId("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();
        TestTemplateResponseDto template = testTemplateService.createTemplate(request).get();

        TestTemplateUpdateDto updateRequest = TestTemplateUpdateDto.builder()
                .id(template.getId() + 1)
                .url("update.com")
                .method("post")
                .body("data=sample")
                .userGroupId("userGroup")
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
                .userGroupId("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();
        TestTemplateResponseDto template = testTemplateService.createTemplate(request).get();

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
                .userGroupId("userGroup")
                .vuser(3)
                .cpuLimit(3)
                .maxRequest(3)
                .maxDuration(3)
                .build();
        TestTemplateResponseDto template = testTemplateService.createTemplate(request).get();

        // When & Then
        assertThrows(GlobalException.class, () -> {
            testTemplateService.deleteTemplate(template.getId() + 1);
        });
    }

}