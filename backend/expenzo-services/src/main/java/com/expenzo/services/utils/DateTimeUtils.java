package com.expenzo.services.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

public class DateTimeUtils {

    public static LocalDateTime[] getBoundaryDateTimes(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return new LocalDateTime[]{
            LocalDateTime.of(year, month, 1, 0, 0),
            LocalDateTime.of(yearMonth.atEndOfMonth(), LocalTime.of(23, 59, 59))
        };
    }
}
