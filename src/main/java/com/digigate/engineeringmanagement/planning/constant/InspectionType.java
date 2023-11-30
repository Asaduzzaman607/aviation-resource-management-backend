package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.Map;

public enum InspectionType {
    GVI(0),
    GV(1),
    OPT(2),
    CHK(3),
    INSP(4),
    DIS(5),
    SVC(6),
    FUT(7),
    FNC (8),
    OPS(9),
    DET(10),
    NDT(11),
    UTI(12);

    private final Integer value;

    InspectionType(Integer value) {
        this.value = value;
    }

    @JsonValue
    public Integer getValue() {
        return this.value;
    }

    private static final Map<Integer, InspectionType> inspectionTypeMap = new HashedMap<>();

    static {
        for (InspectionType inspectionType : InspectionType.values()) {
            inspectionTypeMap.put(inspectionType.getValue(), inspectionType);
        }
    }

    @JsonCreator
    public static InspectionType create(Integer value) {
        if (!inspectionTypeMap.containsKey(value)) {
            throw new EngineeringManagementServerException(
                    ErrorId.UNABLE_TO_PARSE,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return inspectionTypeMap.get(value);
    }


}
