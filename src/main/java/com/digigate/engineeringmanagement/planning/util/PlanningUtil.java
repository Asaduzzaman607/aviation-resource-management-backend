package com.digigate.engineeringmanagement.planning.util;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.planning.constant.ItemColorCode;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class PlanningUtil {

    public static ItemColorCode getColor(LocalDate date, Integer limit) {
        long dayDif = ChronoUnit.DAYS.between(DateUtil.getCurrentUTCDate(), date);
        if ((int) dayDif < ApplicationConstant.NUMBERS.ZERO) {
            return ItemColorCode.RED;
        } else if ((int) dayDif >= ApplicationConstant.NUMBERS.ZERO && (int) dayDif <= limit) {
            return ItemColorCode.AMBER;
        } else {
            return ItemColorCode.GREEN;
        }
    }

    public static String setNullIfEmptyString(String field) {
        if (StringUtils.isBlank(field)) {
            return null;
        }
        return field;
    }

    public static Long calculateRemainingDays(Integer days, LocalDate doneDate, LocalDate fromDate) {
        LocalDateTime endDate = doneDate.plusDays(days).atStartOfDay();
        return Duration.between(fromDate.atStartOfDay(), endDate).toDays();
    }

    public static Double calculateRemainingHour(Double hour, Double doneHour,
                                                Double totalTime) {
        if (Objects.isNull(doneHour)) {
            doneHour = 0.0;
        }
        return DateUtil.subtractTimes(DateUtil.addTimes(hour, doneHour), totalTime);
    }

    public static Double calculateUsedHours(Double outHour, Double inHour){
        return  DateUtil.subtractTimes(outHour,inHour);
    }

    public static Integer calculateUsedCycle(Integer outCycle, Integer inCycle){
        return  outCycle-inCycle;
    }
}
