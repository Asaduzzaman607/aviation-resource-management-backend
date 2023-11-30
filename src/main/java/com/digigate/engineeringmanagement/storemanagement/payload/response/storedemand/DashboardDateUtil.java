package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import java.time.LocalDate;

public class DashboardDateUtil {

    public static LocalDate getBeforeDate(Integer day) {
        return LocalDate.now().minusDays(day);
    }
    public static LocalDate getCurrentDate(){
        return LocalDate.now();
    }

    public static LocalDate getAfterDate(Integer day) {
        return LocalDate.now().plusDays(day);
    }
}
