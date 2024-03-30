package org.benchmarker.bmagent.pref.calculate;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResultCalculatorTest {

    private IResultCalculator resultCalculator;

    @BeforeEach
    void setup(){
        resultCalculator = new ResultCalculator();
    }

    @Test
    @DisplayName("평균 계산")
    void test1(){
        // when
        Double average = resultCalculator.average(1, 2, 3, 4);

        // then
        assertThat(average).isEqualTo(2.5D);
    }

    @Test
    @DisplayName("퍼센타일 계산")
    void test2(){
        // given
        Map<LocalDateTime, Integer> results = new HashMap<>();
        List<Double> percentiles = Arrays.asList(50D, 90D); // 50th, 90th index
        LocalDateTime now = LocalDateTime.now();
        for (int i = 1; i <= 10; i++) {
            results.put(now, i);
            now = now.plusSeconds(1);
        }

        // when
        Map<Double, Integer> percentile = resultCalculator.percentile(results, percentiles, true);

        // then
        Map<Double, Integer> expectResult = Map.of(50D, 5, 90D, 9);
        assertThat(percentile).isEqualTo(expectResult);

        double value = 123.456789;

        // Format the double value to have up to two decimal places
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedValue = df.format(value);

        // Parse the formatted string back to double
        double result = Double.parseDouble(formattedValue);

        System.out.println("Formatted value: " + result);
    }

}