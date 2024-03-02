package org.benchmarker.common.util;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Getter
public class DateUtil {
    /**
     * LocalDateTimeFormat
     * year - month - day - hour - minute - second - nano
     */
    private static final String FORMAT_STRING = "yyyy-MM-dd HH:mm:ss.SSSSSS";
    private static final DateTimeFormatter fixedFormatter = DateTimeFormatter.ofPattern(FORMAT_STRING);
    private static final ChronoUnit truncation = ChronoUnit.MICROS;    // nano seconds removal

    public static LocalDateTime getCurrentTime(){
        return LocalDateTime.now().truncatedTo(truncation);
    }

    public static LocalDateTime getCurrentTime(ChronoUnit truncation){
        return LocalDateTime.now().truncatedTo(truncation);
    }

    public static String getCurrentTimeString(){
        return LocalDateTime.now().truncatedTo(truncation).format(fixedFormatter);
    }

    public static String withFormat(LocalDateTime localDateTime, String format){
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }

}