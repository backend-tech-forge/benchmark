package org.benchmarker.bmagent.pref.calculate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IResultCalculatorTest {

    private IResultCalculator resultCalculator;

    @BeforeEach
    void setup() {
        resultCalculator = mock(IResultCalculator.class);
    }

    @Test
    @DisplayName("평균 계산 테스트")
    public void testAverage() {
        // given
        when(resultCalculator.average(1.0, 2.0, 3.0, 4.0)).thenReturn(2.5);

        // when
        Double result = resultCalculator.average(1.0, 2.0, 3.0, 4.0);

        // then
        assertEquals(2.5, result);
    }

    @Test
    @DisplayName("퍼센타일 계산")
    public void testPercentile() {
        // given
        Map<LocalDateTime, Double> results = new HashMap<>();
        List<Double> percentiles = Arrays.asList(50D, 90D); // 50th, 90th index
        LocalDateTime now = LocalDateTime.now();
        for (int i = 1; i <= 10; i++) {
            results.put(now, (double) i);
            now = now.plusSeconds(1);
        }

        // expected results
        Map<Double, Double> expectResult = Map.of(50D, 5D, 90D, 9D);
        Map<Double, Double> expectResultReverse = Map.of(50D, 5D, 90D, 1D);
        when(resultCalculator.percentile(results, percentiles, false)).thenReturn(expectResult);
        when(resultCalculator.percentile(results, percentiles, true)).thenReturn(expectResultReverse);

        // when
        Map<Double, Double> result = resultCalculator.percentile(results, percentiles, false);
        Map<Double, Double> resultReverse = resultCalculator.percentile(results, percentiles, true);

        // then
        assertThat(result).isEqualTo(expectResult);
        assertThat(resultReverse).isEqualTo(expectResultReverse);
    }
}