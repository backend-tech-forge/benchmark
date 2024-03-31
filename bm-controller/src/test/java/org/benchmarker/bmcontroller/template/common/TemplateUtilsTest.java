package org.benchmarker.bmcontroller.template.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TemplateUtilsTest {

    @Test
    @DisplayName("agent 에서 받은 시간 정확하게 Convert 하는지 확인")
    void convertStringToLocalDateTime() {

        // given
        String dateTimeString = "2024-03-30T15:25:27.410335250";

        // when
        LocalDateTime localDateTime = TemplateUtils.convertStringToLocalDateTime(dateTimeString);

        // then
        assertThat(localDateTime).isEqualTo(LocalDateTime.of(LocalDate.parse("2024-03-30"), LocalTime.parse("15:25:27")));
    }

}