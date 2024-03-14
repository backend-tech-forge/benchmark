package org.benchmarker.template.service;

import lombok.RequiredArgsConstructor;
import org.benchmarker.common.error.ErrorCode;
import org.benchmarker.common.error.GlobalException;
import org.benchmarker.template.controller.dto.TestTemplateRequestDto;
import org.benchmarker.template.controller.dto.TestTemplateResponseDto;
import org.benchmarker.template.controller.dto.TestTemplateUpdateDto;
import org.benchmarker.template.model.TestTemplate;
import org.benchmarker.template.repository.TestTemplateRepository;
import org.benchmarker.user.repository.UserGroupRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestTemplateService extends AbstractTestTemplateService {

    private final TestTemplateRepository testTemplateRepository;
    private final UserGroupRepository userGroupRepository;

    @Override
    public Optional<TestTemplateResponseDto> createTemplate(TestTemplateRequestDto reqTestTemplate) {

        // 등록되어 있는 그룹인지만 검증
        userGroupRepository.findById(reqTestTemplate.getUserGroupId()).orElseThrow(() -> new GlobalException(ErrorCode.GROUP_NOT_FOUND));

        TestTemplate testTemplate = reqTestTemplate.toEntity();
        return Optional.of(testTemplateRepository.save(testTemplate).convertToResponseDto());
    }

    @Override
    public TestTemplateResponseDto getTemplate(Integer id) {
        // Template 존재하는지 확인 후에 리턴
        TestTemplate testTemplate = testTemplateRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));
        return testTemplate.convertToResponseDto();
    }

    @Override
    public List<TestTemplateResponseDto> getTemplates() {

        List<TestTemplate> testTemplates = testTemplateRepository.findAll();
        List<TestTemplateResponseDto> response = new ArrayList<>();
        for (TestTemplate testTemplate : testTemplates) {
            response.add(testTemplate.convertToResponseDto());
        }

        return response;
    }

    @Override
    public Optional<TestTemplateResponseDto> updateTemplate(TestTemplateUpdateDto testTemplate) throws Exception {

        // 템플릿이 존재하는지 먼저 파악.
        TestTemplate preTestTemplate = testTemplateRepository.findById(testTemplate.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        preTestTemplate.update(testTemplate);

        return Optional.of(testTemplateRepository.save(preTestTemplate).convertToResponseDto());
    }

    @Override
    public void deleteTemplate(Integer id) {

        TestTemplate preTestTemplate = testTemplateRepository.findById(id).orElseThrow(() -> new GlobalException(ErrorCode.TEMPLATE_NOT_FOUND));

        testTemplateRepository.delete(preTestTemplate);
    }
}
