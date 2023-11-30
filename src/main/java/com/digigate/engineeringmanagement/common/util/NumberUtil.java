package com.digigate.engineeringmanagement.common.util;

import com.digigate.engineeringmanagement.common.config.constant.OperatorType;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.NumberConstant;
import com.digigate.engineeringmanagement.planning.constant.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Number Utils
 *
 * @author Pranoy Das
 */
public class NumberUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(NumberUtil.class);
    private static final String DOT_REGEX = "\\.";
    private static final char CHAR_ZERO = '0';

    public static final Integer convertToInteger(String numberStr) {
        return convertToInteger(numberStr, -1);
    }

    public static final Integer convertToInteger(String numberStr, Integer defaultValue) {
        try {
            if (StringUtils.isNotBlank(numberStr)) {
                return Integer.valueOf(numberStr);
            } else {
                return defaultValue;
            }
        } catch (NumberFormatException ex) {
            LOGGER.info("Can't convert to integer from given string {}", numberStr);
            return defaultValue;
        }
    }

    public static final Integer getValidPageNumber(Integer number) {
        if (Objects.isNull(number)) {
            return 1;
        }
        return number <= 0 ? 1 : number - 1;
    }

    public static final Integer getValidPageSize(Integer number) {
        if (Objects.isNull(number)) {
            return 1;
        }
        return number <= 0 ? 1 : number;
    }

    public static <T> T getDefaultIfNull(T input, T defaultVal) {
        return Optional.ofNullable(input).orElse(defaultVal);
    }

    public static Double parseDoubleValue(String stringValue) {
        if (StringUtils.isBlank(stringValue)) {
            return null;
        }
        return Double.valueOf(stringValue);
    }

    public static <T> T parseValue(String value, Class<T> classType) {
        if (classType == Double.class) {
            return (T) parseDoubleValue(value);
        } else if (classType == Long.class) {
            return (T) parseLongValue(value);
        } else if (classType == Integer.class) {
            return (T) parseIntValue(value);
        } else if (classType == Boolean.class) {
            return (T) parseBooleanValue(value);
        } else if (classType == LocalDate.class) {
            return (T) DateUtil.parseToLocalDate(value, DateTimeFormatter.ofPattern(ApplicationConstant.DATE_FORMAT));
        } else if (classType == LocalDateTime.class) {
            return (T) DateUtil.parseToLocalDateTime(value, DateTimeFormatter.ofPattern(ApplicationConstant.DATE__TIME_FORMAT));
        } else if(classType == PartClassification.class) {
            return (T) PartClassification.getByName(value);
        } else if(classType == ModelType.class){
            return (T) ModelType.getByName(value);
        } else if(classType == LifeCodes.class){
            return (T) LifeCodes.getByName(value);
        } else if(classType == LifeLimitUnit.class){
            return (T) LifeLimitUnit.getByName(value);
        } else if(classType == TaskStatusEnum.class){
            return (T) TaskStatusEnum.getByName(value);
        } else if(classType == RepetitiveTypeEnum.class){
            return (T) RepetitiveTypeEnum.getByName(value);
        } else if(classType == IntervalType.class){
            return (T) IntervalType.getByName(value);
        } else if(classType == EffectivityType.class){
            return (T) EffectivityType.getByName(value);
        }
        return (T) value;
    }

    public static <T> List<T> getListValue(String listValue, Class<T> classType) {
        List<String> clientDataList = Arrays.asList(listValue.split(ApplicationConstant.COMMA_SEPARATOR));
        List<T> listItems = new ArrayList<>();
        clientDataList.forEach(clientData -> {
            if (StringUtils.isNotBlank(clientData)) {
                clientData = clientData.trim();
                listItems.add(parseValue(clientData, classType));
            }
        });
        return listItems;
    }

    public static Long parseLongValue(String stringValue) {
        if (StringUtils.isBlank(stringValue)) {
            return null;
        }
        return Long.valueOf(stringValue);
    }

    public static Integer parseIntValue(String stringValue) {
        if (StringUtils.isBlank(stringValue)) {
            return null;
        }
        return Integer.valueOf(stringValue);
    }

    public static Boolean parseBooleanValue(String stringValue) {
        if (StringUtils.isBlank(stringValue)) {
            return null;
        }
        return Boolean.valueOf(stringValue);
    }

    public static Boolean checkValidAirTime(Double time) {
        if (Objects.isNull(time)) {
            LOGGER.info("invalid time(time is null)");
            return false;
        }
        String timeStr = StringUtil.valueOf(time);
        String[] strTimeArray = timeStr.split(DOT_REGEX);
        long value = 0L;
        if (strTimeArray.length == 2) {
            if (strTimeArray[1].length() > 2 || strTimeArray[1].length() < 1) {
                return false;
            }
            value = (strTimeArray[1].charAt(0) - CHAR_ZERO) * 10L;
            if (strTimeArray[1].length() == 2) {
                value += (strTimeArray[1].charAt(1) - CHAR_ZERO);
            }

            if (value > 59) {
                LOGGER.info("invalid time");
                return false;
            }

        }
        return true;
    }

    public static boolean compareValue(double firstValue, double secondValue, OperatorType operatorType) {
        if(operatorType.equals(OperatorType.GREATER_THAN)) {
            return firstValue > secondValue;
        } else if(operatorType.equals(OperatorType.GREATER_THAN_OR_EQUAL)) {
            return firstValue >= secondValue;
        } else if(operatorType.equals(OperatorType.LESS_THAN)) {
            return firstValue < secondValue;
        } else if(operatorType.equals(OperatorType.LESS_THAN_OR_EQUAL)) {
            return firstValue <= secondValue;
        } else if( operatorType.equals(OperatorType.EQUAL)) {
            return firstValue == secondValue;
        }
        return firstValue != secondValue;
    }

    public static boolean compareValue(long firstValue, long secondValue, OperatorType operatorType) {
        if(operatorType.equals(OperatorType.GREATER_THAN)) {
            return firstValue > secondValue;
        } else if(operatorType.equals(OperatorType.GREATER_THAN_OR_EQUAL)) {
            return firstValue >= secondValue;
        } else if(operatorType.equals(OperatorType.LESS_THAN)) {
            return firstValue < secondValue;
        } else if(operatorType.equals(OperatorType.LESS_THAN_OR_EQUAL)) {
            return firstValue <= secondValue;
        } else if( operatorType.equals(OperatorType.EQUAL)) {
            return firstValue == secondValue;
        }
        return firstValue != secondValue;
    }

    public static boolean compareValue(int firstValue, int secondValue, OperatorType operatorType) {
        if(operatorType.equals(OperatorType.GREATER_THAN)) {
            return firstValue > secondValue;
        } else if(operatorType.equals(OperatorType.GREATER_THAN_OR_EQUAL)) {
            return firstValue >= secondValue;
        } else if(operatorType.equals(OperatorType.LESS_THAN)) {
            return firstValue < secondValue;
        } else if(operatorType.equals(OperatorType.LESS_THAN_OR_EQUAL)) {
            return firstValue <= secondValue;
        } else if( operatorType.equals(OperatorType.EQUAL)) {
            return firstValue == secondValue;
        }
        return firstValue != secondValue;
    }

    public static boolean compareValue(boolean firstValue, boolean secondValue, OperatorType operatorType) {
        if(operatorType.equals(OperatorType.EQUAL)) {
            return firstValue == secondValue;
        }
        return firstValue != secondValue;
    }

    public static boolean compareValue(String firstValue, String secondValue, OperatorType operatorType) {
        if(operatorType.equals(OperatorType.EQUAL)) {
            return firstValue.equals(secondValue);
        }
        return !firstValue.equals(secondValue);
    }

    public static boolean compareValue(PartClassification firstValue,
                                       PartClassification secondValue, OperatorType operatorType) {
        if(operatorType.equals(OperatorType.EQUAL)) {
            return firstValue.equals(secondValue);
        }
        return !firstValue.equals(secondValue);
    }

    public static boolean compareValue(ModelType fistValue, ModelType secondValue, OperatorType operatorType){
        if(operatorType.equals(OperatorType.EQUAL)){
            return fistValue.equals(secondValue);
        }
        return !fistValue.equals(secondValue);
    }

    public static boolean compareValue(LifeCodes firstValue, LifeCodes secondValue, OperatorType operatorType){
        if(operatorType.equals(OperatorType.EQUAL)){
            return firstValue.equals(secondValue);
        }
        return !firstValue.equals(secondValue);
    }

    public static boolean compareValue(LifeLimitUnit firstValue, LifeLimitUnit secondValue, OperatorType operatorType){
        if(operatorType.equals(OperatorType.EQUAL)){
            return firstValue.equals(secondValue);
        }
        return !firstValue.equals(secondValue);
    }

    public static boolean compareValue(EffectivityType fistValue, EffectivityType secondValue,
                                       OperatorType operatorType){
        if(operatorType.equals(OperatorType.EQUAL)){
            return fistValue.equals(secondValue);
        }
        return !fistValue.equals(secondValue);
    }

    public static boolean compareValue(IntervalType fistValue, IntervalType secondValue,
                                       OperatorType operatorType){
        if(operatorType.equals(OperatorType.EQUAL)){
            return fistValue.equals(secondValue);
        }
        return !fistValue.equals(secondValue);
    }

    public static boolean compareValue(RepetitiveTypeEnum fistValue, RepetitiveTypeEnum secondValue,
                                       OperatorType operatorType){
        if(operatorType.equals(OperatorType.EQUAL)){
            return fistValue.equals(secondValue);
        }
        return !fistValue.equals(secondValue);
    }

    public static boolean compareValue(TaskStatusEnum fistValue, TaskStatusEnum secondValue,
                                        OperatorType operatorType){
        if(operatorType.equals(OperatorType.EQUAL)){
            return fistValue.equals(secondValue);
        }
        return !fistValue.equals(secondValue);
    }

    public static String formatDecimalValue(Double value, String pattern) {
        if (Objects.isNull(value)) {
            LOGGER.error("decimal value is null: {}", value);
            return null;
        }

        if (StringUtils.isBlank(pattern)) {
            pattern = NumberConstant.TWO_DECIMAL_FORMAT;
        }

        DecimalFormat decimalFormat = new DecimalFormat(pattern);

        return decimalFormat.format(value);
    }
 }
