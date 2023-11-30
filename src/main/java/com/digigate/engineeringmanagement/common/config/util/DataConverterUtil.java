package com.digigate.engineeringmanagement.common.config.util;


import com.digigate.engineeringmanagement.common.config.constant.DataType;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.planning.constant.*;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


public class DataConverterUtil {

    public static <T> T getValueForRequiredField(String stringValue, List<String> errorMessages,
                                                 int rowNumber,
                                                 String columnName, DataType type, DataType dataType) {
        T value = getValue(stringValue, type, dataType);
        if (Objects.isNull(value)) {
            errorMessages.add(String.format("Invalid data format for column: {%s} at row: {%s}", columnName, rowNumber));
            return null;
        }
        return value;
    }

    public static <T> T getValue(String stringValue, DataType type, DataType dataType) {
        try {
            Class clazz = getClassType(type);
            if (clazz == List.class) {
                return (T) NumberUtil.getListValue(stringValue, getClassType(dataType));
            } else {
                return (T) NumberUtil.parseValue(stringValue, clazz);
            }
        } catch (Exception exception) {
            return null;
        }
    }


    public static <T> T getValidDataForOptionalColumn(String stringValue, DataType type, DataType dataType) {
        if (StringUtils.isBlank(stringValue)) {
            return null;
        }
        return getValue(stringValue, type, dataType);
    }

    public static <T> T getValueForOptionalField(String stringValue,
                                                 List<String> errorMessages,
                                                 int rowNumber, String columnName, DataType type, DataType dataType) {
        if (StringUtils.isBlank(stringValue)) {
            return null;
        }
        try {
            return getValueForRequiredField(stringValue, errorMessages, rowNumber, columnName, type, dataType);
        } catch (Exception exception) {
            return null;
        }
    }

    private static Class getClassType(DataType type) {
        switch (type) {
            case DOUBLE:
                return Double.class;
            case LONG:
                return Long.class;
            case INT:
                return Integer.class;
            case BOOLEAN:
                return Boolean.class;
            case DATE:
                return LocalDate.class;
            case DATE_TIME:
                return LocalDateTime.class;
            case LIST:
                return List.class;
            case PART_CLASSIFICATION:
                return PartClassification.class;
            case LIFE_LIMIT_UNIT:
                return LifeLimitUnit.class;
            case MODEL_TYPE:
                return ModelType.class;
            case LIFE_CODES:
                return LifeCodes.class;
            case EFFECTIVITY_TYPE:
                return EffectivityType.class;
            case INTERVAL_TYPE:
                return IntervalType.class;
            case REPETITIVE_TYPE:
                return RepetitiveTypeEnum.class;
            case TASK_STATUS:
                return TaskStatusEnum.class;
            case STRING:
            default:
                return String.class;
        }
    }
}
