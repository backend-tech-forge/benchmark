package org.benchmarker.bmcontroller.template.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TemplateUtils {

    public static LocalDateTime convertStringToLocalDateTime(String dateTimeString) {
        try{
            LocalDate datePart = LocalDate.parse(dateTimeString.split("T")[0]);
            LocalTime timePart = LocalTime.parse(dateTimeString.split("T")[1].substring(0, 8));
            return LocalDateTime.of(datePart, timePart);
        }catch (Exception e){
            return null;
        }
    }
}
