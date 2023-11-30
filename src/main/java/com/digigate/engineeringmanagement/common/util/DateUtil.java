package com.digigate.engineeringmanagement.common.util;

import com.digigate.engineeringmanagement.common.config.constant.OperatorType;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.planning.constant.HourCalculationType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;

/**
 * Date Utils
 *
 * @author ashinisingha
 */
public class DateUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);
    private static final Integer MILLISECONDS_IN_MINUTE = 60000;
    private static final Integer MINUTE_IN_HOUR = 60;
    private static final Double HUNDRED = 100.00;
    private static final String DOT_REGEX = "\\.";
    private static final char CHAR_ZERO = '0';

    private static final Long TWENTY_FOUR_MONTHS = 731L;

    private static final Double SIXTY_IN_DOUBLE = 60.00;

    private static final Double ZERO_IN_DOUBLE = 0.00;
    private static final String ZERO_IN_STRING = "0.00";
    private static final Double HUNDRED_DOUBLE = 100.00;
    private static final Long TWELVE_MONTHS = 365L;

    private static final Long ONE_MONTH = 30L;
    private static final DecimalFormat TWO_DIGIT_DOUBLE_FORMAT = new DecimalFormat("00.00");

    /**
     * Convert Local Date time to minutes
     *
     * @param localDateTime {@link LocalDateTime}
     * @return {@link  Long}
     */
    public static Long convertToMinutes(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime).getTime() / MILLISECONDS_IN_MINUTE;
    }

    public static Double twoDigitDoubleValueOf(Double value) {
        return Double.valueOf((TWO_DIGIT_DOUBLE_FORMAT.format(value)));
    }

    public static Long convertToMinutes(Double time) {
        if (Objects.isNull(time)) {
            LOGGER.info("invalid time!");
            return 0L;
        }
        String timeStr = String.valueOf(time);
        String[] strTimeArray = timeStr.split(DOT_REGEX);
        long hour = Math.abs(Long.parseLong(strTimeArray[0]));
        if (strTimeArray.length == 2) {
            long minute = (strTimeArray[1].charAt(0) - CHAR_ZERO) * 10L;
            if (strTimeArray[1].length() > 1) {
                minute += (strTimeArray[1].charAt(1) - CHAR_ZERO);
            }
            long hourToMinute = hour * 60L;
            return hourToMinute + minute;
        }
        LOGGER.info("invalid time!");
        return 0L;
    }

    public static Boolean isValidTime(Double time) {
        if (Objects.isNull(time)) {
            return false;
        }
        String timeStr = String.valueOf(time);
        String[] strTimeArray = timeStr.split(DOT_REGEX);
        if (strTimeArray.length == 2) {
            long value = (strTimeArray[1].charAt(0) - CHAR_ZERO) * 10L;
            if (strTimeArray[1].length() == 2) {
                value += (strTimeArray[1].charAt(1) - CHAR_ZERO);
            }
            if (value >= 0 && value < 60) return true;
            else return false;

        }
        return true;
    }


    /**
     * duration between two local date time in minutes
     *
     * @param startDateTime {@link  LocalDateTime}
     * @param endDateTime   {@link  LocalDateTime}
     * @return {@link Long}
     */
    public static Long duration(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (Objects.isNull(startDateTime) || Objects.isNull(endDateTime)) {
            LOGGER.info("start/end date time is null");
            return null;
        }

        if (endDateTime.isBefore(startDateTime)) {
            throw new EngineeringManagementServerException(
                    ErrorId.START_TIME_MUST_BE_BEFORE_END_TIME,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
        return convertToMinutes(endDateTime) - convertToMinutes(startDateTime);
    }

    /**
     * This method is responsible for converting minutes to
     * Hour.Minutes ; A.BC = A hour , BC minutes ; 1.40 = 1 hour 40 minutes
     *
     * @param minutes {@link Long}
     * @return {@link  Double}
     */
    public static Double convertMinutesToHour(Long minutes) {
        long hours = minutes / MINUTE_IN_HOUR;
        minutes %= MINUTE_IN_HOUR;
        return twoDigitDoubleValueOf(hours + (minutes / HUNDRED));
    }

    /**
     * this method is used to convert from string date to local date
     *
     * @param dateStr           {@link String}
     * @param dateTimeFormatter {@link DateTimeFormatter}
     * @return {@link LocalDate}
     */
    public static LocalDate parseToLocalDate(String dateStr, DateTimeFormatter dateTimeFormatter) {
        if (StringUtils.isBlank(dateStr)) {
            throw new EngineeringManagementServerException(
                    ErrorId.FAILED_TO_CONVERT_FROM_STRING_TO_LOCAL_DATE, HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
        try {
            return LocalDate.parse(dateStr, dateTimeFormatter);
        } catch (Exception exception) {
            throw new EngineeringManagementServerException(
                    ErrorId.FAILED_TO_CONVERT_FROM_STRING_TO_LOCAL_DATE, HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    /**
     * this method is used to convert from string date time to local date time
     *
     * @param dateStr           {@link String}
     * @param dateTimeFormatter {@link DateTimeFormatter}
     * @return {@link LocalDate}
     */
    public static LocalDateTime parseToLocalDateTime(String dateStr, DateTimeFormatter dateTimeFormatter) {
        if (StringUtils.isBlank(dateStr)) {
            throw new EngineeringManagementServerException(
                    ErrorId.FAILED_TO_CONVERT_FROM_STRING_TO_LOCAL_DATE, HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
        try {
            return LocalDateTime.parse(dateStr, dateTimeFormatter);
        } catch (Exception exception) {
            throw new EngineeringManagementServerException(
                    ErrorId.FAILED_TO_CONVERT_FROM_STRING_TO_LOCAL_DATE, HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    public static boolean compareValue(LocalDate firstDate, LocalDate secondDate, OperatorType operatorType) {
        if (operatorType.equals(OperatorType.GREATER_THAN)) {
            return firstDate.isAfter(secondDate);
        } else if (operatorType.equals(OperatorType.GREATER_THAN_OR_EQUAL)) {
            return (firstDate.isAfter(secondDate) || firstDate.equals(secondDate));
        } else if (operatorType.equals(OperatorType.LESS_THAN)) {
            return firstDate.isBefore(secondDate);
        } else if (operatorType.equals(OperatorType.LESS_THAN_OR_EQUAL)) {
            return ((firstDate.isBefore(secondDate)) || firstDate.equals(secondDate));
        } else if (operatorType.equals(OperatorType.EQUAL)) {
            return firstDate.equals(secondDate);
        }
        return !firstDate.equals(secondDate);
    }

    public static boolean compareValue(LocalDateTime firstDateTime,
                                       LocalDateTime secondDateTime, OperatorType operatorType) {
        if (operatorType.equals(OperatorType.GREATER_THAN)) {
            return firstDateTime.isAfter(secondDateTime);
        } else if (operatorType.equals(OperatorType.GREATER_THAN_OR_EQUAL)) {
            return (firstDateTime.isAfter(secondDateTime) || firstDateTime.equals(secondDateTime));
        } else if (operatorType.equals(OperatorType.LESS_THAN)) {
            return firstDateTime.isBefore(secondDateTime);
        } else if (operatorType.equals(OperatorType.LESS_THAN_OR_EQUAL)) {
            return ((firstDateTime.isBefore(secondDateTime)) || secondDateTime.equals(firstDateTime));
        } else if (operatorType.equals(OperatorType.EQUAL)) {
            return firstDateTime.equals(secondDateTime);
        }
        return !firstDateTime.equals(secondDateTime);
    }

    public static Double addTimes(Double time1, Double time2) {
        return DateUtil.convertMinutesToHour(DateUtil.convertToMinutes(time1) + DateUtil.convertToMinutes(time2));
    }

    public static Double subtractTimes(Double time1, Double time2) {
        return DateUtil.convertMinutesToHour(DateUtil.convertToMinutes(time1) - DateUtil.convertToMinutes(time2));
    }

    public static void isValidateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (toDate.isBefore(fromDate)) {
            throw new EngineeringManagementServerException(
                    ErrorId.FROM_DATE_MUST_BE_LESS_THAN_OR_EQUAL_TO_DATE, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (ChronoUnit.DAYS.between(fromDate, toDate) > TWENTY_FOUR_MONTHS) {
            throw new EngineeringManagementServerException(
                    ErrorId.DATE_RANGE_LIMIT_EXCEED, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    public static void isValidateDateRangeWith12Months(LocalDate fromDate, LocalDate toDate) {
        if (toDate.isBefore(fromDate)) {
            throw new EngineeringManagementServerException(
                    ErrorId.FROM_DATE_MUST_BE_LESS_THAN_OR_EQUAL_TO_DATE, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (ChronoUnit.DAYS.between(fromDate, toDate) > TWELVE_MONTHS) {
            throw new EngineeringManagementServerException(
                    ErrorId.DATE_RANGE_LIMIT_EXCEED_FOR_12_MONTH, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    public static void isValidateDateRangeWith1Months(LocalDate fromDate, LocalDate toDate) {
        if (toDate.isBefore(fromDate)) {
            throw new EngineeringManagementServerException(
                    ErrorId.FROM_DATE_MUST_BE_LESS_THAN_OR_EQUAL_TO_DATE, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (ChronoUnit.DAYS.between(fromDate, toDate) > ONE_MONTH) {
            throw new EngineeringManagementServerException(
                    ErrorId.DATE_RANGE_LIMIT_EXCEED_FOR_1_MONTH, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    public static LocalDate getCurrentUTCDate() {
        return LocalDateTime.now(ZoneOffset.UTC).toLocalDate();
    }

    public static LocalDateTime getUpdatedDateTime() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public static Double calculateHour(Double time1, Double time2, HourCalculationType hourCalculationType) {
        Long min1 = DateUtil.convertToMinutes(time1);
        Long min2 = DateUtil.convertToMinutes(time2);
        Long val = null;
        if (hourCalculationType.equals(HourCalculationType.ADD)) {
            val = min1 + min2;
        } else if (hourCalculationType.equals(HourCalculationType.SUBTRACT)) {
            val = min1 - min2;
        } else if (hourCalculationType.equals(HourCalculationType.DIVIDE)) {
            val = min1 / min2;
        } else if (hourCalculationType.equals(HourCalculationType.DAY_COUNT)) {
            val = min1 / min2;
            return val.doubleValue();
        }
        if (Objects.nonNull(val)) {
            return DateUtil.convertMinutesToHour(val);
        } else return null;
    }

    public static Boolean isvoidWithinDateRange(LocalDate inputDate, LocalDate startDate, LocalDate endDate) {
        if (Objects.isNull(inputDate) || Objects.isNull(startDate) || Objects.isNull(endDate)) {
            return Boolean.FALSE;
        }
        return !(inputDate.isBefore(startDate) || inputDate.isAfter(endDate));
    }

    public static Double convertMinuteIntoHundredValue(Double hourMinute) {

        double hourPart = hourMinute.intValue();
        double minutePart = hourMinute - hourPart;
        if (minutePart == ZERO_IN_DOUBLE) {
            return twoDigitDoubleValueOf(hourMinute);
        }
        double minInHundred = (minutePart * HUNDRED_DOUBLE) / SIXTY_IN_DOUBLE;
        double finalValue = hourPart + minInHundred;
        return twoDigitDoubleValueOf(finalValue);
    }

    public static void isValidFromDate(LocalDate toDate) {
        LocalDate currentDate = DateUtil.getCurrentUTCDate();
        LocalDate currentMonthStart = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1);
        if (!toDate.isBefore(currentMonthStart)) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_TO_DATE, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

    }

    public static Boolean isBeforeCurrentMonth(LocalDate date) {
        LocalDate currentDate = DateUtil.getCurrentUTCDate();
        LocalDate currentMonthStart = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1);
        return currentMonthStart.isAfter(date);
    }

    public static double convertHourMinutesToDecimalHourMinutes(Double time) {
        if (Objects.isNull(time)) {
            return 0.0;
        }
        DecimalFormat df = new DecimalFormat(ZERO_IN_STRING);
        String timeStr = df.format(time);
        String[] parts = timeStr.split(DOT_REGEX);
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return twoDigitDoubleValueOf(hours + (minutes / SIXTY_IN_DOUBLE));
    }

    public static long roundUp(double value) {
        return Math.round(value);
    }
}
