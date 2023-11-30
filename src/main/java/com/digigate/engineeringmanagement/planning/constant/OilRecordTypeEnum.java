package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Oil Record Type Enum
 *
 * @author Sayem Hasnat
 */
public enum OilRecordTypeEnum {
    ON_ARRIVAL(1),
    UPLIFT(2),
    OIL_RECORD_TOTAL_TYPE(3);
    private final int value;
    OilRecordTypeEnum(int value) {
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return this.value;
    }


    private static final Map<Integer, OilRecordTypeEnum> oilRecordTypeEnumMap = new HashMap<>();

    static {
        for (OilRecordTypeEnum d : OilRecordTypeEnum.values()) {
            oilRecordTypeEnumMap.put(d.getValue(), d);
        }
    }

    @JsonCreator
    public static OilRecordTypeEnum create(Integer id) {
        if (!oilRecordTypeEnumMap.containsKey(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.DATA_NOT_FOUND,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return oilRecordTypeEnumMap.get(id);
    }
}

