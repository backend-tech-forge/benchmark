package org.benchmarker.bmcontroller.template.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.benchmarker.bmcommon.dto.TemplateInfo;
import org.benchmarker.bmcontroller.common.error.ErrorCode;
import org.benchmarker.bmcontroller.common.error.GlobalException;
import org.benchmarker.bmcontroller.common.util.JsonMapper;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.bmcontroller.template.controller.dto.TestTemplateUpdateDto;
import org.benchmarker.bmcontroller.template.model.TestTemplate;
import org.benchmarker.bmcontroller.template.repository.TestTemplateRepository;
import org.benchmarker.bmcontroller.user.controller.dto.GroupInfo;
import org.benchmarker.bmcontroller.user.repository.UserGroupJoinRepository;
import org.benchmarker.bmcontroller.user.repository.UserGroupRepository;
import org.benchmarker.bmcontroller.user.util.UserServiceUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.benchmarker.bmcommon.util.NoOp.noOp;

@Service
@RequiredArgsConstructor
public class TestTemplateService extends AbstractTestTemplateService {

    private final TestTemplateRepository testTemplateRepository;

    private final UserGroupRepository userGroupRepository;

    private final UserServiceUtils userServiceUtils;
    private final JsonMapper jsonMapper;

    @Override
    public TemplateInfo getTemplateInfo(String userId, Integer templateId)
        throws Exception {
        userServiceUtils.verifyAccessTemplate(userId, templateId);
        TestTemplate testTemplate = testTemplateRepository.findById(templateId)
            .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        Map<String, Object> reqBody = jsonMapper.jsonStringToMap(testTemplate.getBody());
        Map<String, String> reqHeader = jsonMapper.jsonStringToMapString(
            testTemplate.getHeaders());

        return TemplateInfo.builder()
            .id(String.valueOf(testTemplate.getId()))
            .url(testTemplate.getUrl())
            .headers(reqHeader)
            .body(reqBody)
            .method(testTemplate.getMethod())
            .prepareScript(testTemplate.getPrepareScript())
            .vuser(testTemplate.getVuser())
            .maxRequest(testTemplate.getMaxRequest())
            .maxDuration(Duration.ofSeconds(testTemplate.getMaxDuration()))
            .description("default description")
            .build();

    }

    private final UserGroupJoinRepository userGroupJoinRepository;

    @Override
    public Optional<TestTemplateResponseDto> createTemplate(
        TestTemplateRequestDto reqTestTemplate) throws JsonProcessingException {

        // 등록되어 있는 그룹인지만 검증
        userGroupRepository.findById(reqTestTemplate.getUserGroupId())
            .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));


        TestTemplate testTemplate = reqTestTemplate.toEntity();
        return Optional.of(testTemplateRepository.save(testTemplate).convertToResponseDto());
    }

    @Override
    public TestTemplateResponseDto getTemplate(Integer id) {
        // Template 존재하는지 확인 후에 리턴
        TestTemplate testTemplate = testTemplateRepository.findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));
        return testTemplate.convertToResponseDto();
    }

    /**
     * <strong>ADMIN ONLY</strong>
     * <p>
     * Get all {@link TestTemplateResponseDto} for admin
     *
     * @return
     */
    @Override
    public List<TestTemplateResponseDto> getAllTemplatesAdmin() {

        List<TestTemplate> testTemplates = testTemplateRepository.findAll();
        List<TestTemplateResponseDto> response = new ArrayList<>();
        for (TestTemplate testTemplate : testTemplates) {
            response.add(testTemplate.convertToResponseDto());
        }

        return response;
    }

    public List<TestTemplateResponseDto> getTemplates(String groupId, String userId) {
        GroupInfo groupInfo = userServiceUtils.getGroupInfo(groupId, userId);
        return groupInfo.getTemplates();
    }

    public List<TestTemplateResponseDto> getTemplates(String groupId) {
        return testTemplateRepository.findAllByUserGroupId(groupId)
            .stream().map(TestTemplate::convertToResponseDto).toList();
    }

    @Override
    public Optional<TestTemplateResponseDto> updateTemplate(TestTemplateUpdateDto testTemplate, String userId) {

        // 템플릿이 존재하는지 먼저 파악.
        TestTemplate preTestTemplate = testTemplateRepository.findById(testTemplate.getId())
            .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        // 등록되어 있는 그룹인지만 검증
        userGroupRepository.findById(testTemplate.getUserGroupId())
                .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));

        userGroupJoinRepository.findByUserIdAndUserGroupId(userId, testTemplate.getUserGroupId()).ifPresentOrElse(
                (u) -> {
                    noOp();
                },
                () -> {
                    throw new GlobalException(ErrorCode.USER_NOT_IN_GROUP);
                });

        preTestTemplate.update(testTemplate);

        return Optional.of(testTemplateRepository.save(preTestTemplate).convertToResponseDto());
    }

    @Override
    public void deleteTemplate(Integer id, String userId) {

        TestTemplate preTestTemplate = testTemplateRepository.findById(id)
            .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        // 등록되어 있는 그룹인지만 검증
        userGroupRepository.findById(preTestTemplate.getUserGroup().getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));

        userGroupJoinRepository.findByUserIdAndUserGroupId(userId, preTestTemplate.getUserGroup().getId()).ifPresentOrElse(
                (u) -> {
                    noOp();
                },
                () -> {
                    throw new GlobalException(ErrorCode.USER_NOT_IN_GROUP);
                });

        testTemplateRepository.delete(preTestTemplate);
    }
}
