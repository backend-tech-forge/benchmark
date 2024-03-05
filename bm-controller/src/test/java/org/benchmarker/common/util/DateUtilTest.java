package org.benchmarker.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DateUtilTest {

    @Test
    @DisplayName("현재 시간을 반환한다")
    public void testGetCurrentTime() {
        // When
        LocalDateTime currentTime = DateUtil.getCurrentTime();

        // Then
        assertNotNull(currentTime);
    }

    @Test
    @DisplayName("현재 시간 truncate 반환 성공")
    public void testGetCurrentTimeWithTruncation() {
        // Given
        ChronoUnit truncation = ChronoUnit.MINUTES;

        // When
        LocalDateTime currentTime = DateUtil.getCurrentTime(truncation);

        // Then
        assertNotNull(currentTime);
        assertEquals(0, currentTime.getSecond());
        assertEquals(0, currentTime.getNano());
    }

    @Test
    @DisplayName("현재 시간 문자열포멧 반환 성공")
    public void testGetCurrentTimeString() {
        // When
        String currentTimeString = DateUtil.getCurrentTimeString();

        // Then
        assertNotNull(currentTimeString);
        assertEquals(26, currentTimeString.length());
    }

    @Test
    @DisplayName("포멧된 날짜 반환 성공")
    public void testWithFormat() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2022, 1, 1, 12, 30, 45);

        // When
        String formattedDateTime1 = DateUtil.withFormat(dateTime, "yyyy-MM-dd mm:ss");
        String formattedDateTime2 = DateUtil.withFormat(dateTime, "yyyy-MM-dd");

        // Then
        assertNotNull(formattedDateTime1);
        assertEquals("2022-01-01 30:45", formattedDateTime1);
        assertNotNull(formattedDateTime2);
        assertEquals("2022-01-01", formattedDateTime2);
    }

}