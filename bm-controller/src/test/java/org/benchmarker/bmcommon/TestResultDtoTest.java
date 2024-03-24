package org.benchmarker.bmcommon;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.benchmarker.bmcommon.dto.TestResult;
import org.benchmarker.bmcommon.util.RandomUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestResultDtoTest {

    @Test
    @DisplayName("TestResult DTO 역직렬화/직렬화 테스트")
    public void test1() throws JsonProcessingException {
        // Given
        TestResult dto1 = RandomUtils.generateRandomTestResult();
        ObjectMapper objectMapper = new ObjectMapper();

        // When
        String serialized = objectMapper.writeValueAsString(dto1);
        TestResult deserialized = objectMapper.readValue(serialized, TestResult.class);

        // Then
        assertThat(deserialized).isEqualTo(dto1);
    }

}
