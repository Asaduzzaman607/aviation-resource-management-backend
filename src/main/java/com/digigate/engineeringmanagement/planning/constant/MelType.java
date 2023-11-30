package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum MelType {
    NONE(0),
    ADD(1),
    CLEAR(2);

    private final Integer amlType;


    MelType(Integer amlType) {
        this.amlType = amlType;
    }

    @JsonValue
    public Integer getAmlType() {
        return this.amlType;
    }


    private static final Map<Integer, MelType> melTypeMap = new HashMap<>();

    static {
        for (MelType m : MelType.values()) {
            melTypeMap.put(m.getAmlType(), m);
        }
    }

    public static MelType get(Integer id) {
        if (!melTypeMap.containsKey(id)) {
            throw  EngineeringManagementServerException.badRequest(ErrorId.INVALID_MEL_TYPE);
        }
        return melTypeMap.get(id);
    }
}
