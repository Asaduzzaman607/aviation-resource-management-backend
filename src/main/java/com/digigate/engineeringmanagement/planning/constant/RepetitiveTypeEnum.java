package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Interval Type Enum
 *
 * @author Pranoy Das
 */
public enum RepetitiveTypeEnum {
    ONE_TIME(0),
    REPETITIVE(1);

    private Integer intervalEnum;

    RepetitiveTypeEnum(int intervalEnum) {
        this.intervalEnum = intervalEnum;
    }

    public Integer getIntervalEnum() {
        return intervalEnum;
    }

    private static final Map<Integer, RepetitiveTypeEnum> repetitiveTypeEnumMap = new HashMap<>();

    static {
        for (RepetitiveTypeEnum m : RepetitiveTypeEnum.values()) {
            repetitiveTypeEnumMap.put(m.getIntervalEnum(), m);
        }
    }

    public static RepetitiveTypeEnum get(Integer id) {
        if (!repetitiveTypeEnumMap.containsKey(id)) {
            throw  EngineeringManagementServerException.badRequest(ErrorId.INVALID_TASK_REPETITIVE_TYPE);
        }
        return repetitiveTypeEnumMap.get(id);
    }

    public static RepetitiveTypeEnum getByName(String name){
        for(RepetitiveTypeEnum repetitiveTypeEnum : RepetitiveTypeEnum.values()){
            if(StringUtils.equals(repetitiveTypeEnum.name(), name)){
                return repetitiveTypeEnum;
            }
        }
        return null;
    }
}
