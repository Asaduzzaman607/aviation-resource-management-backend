package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * aml type enum
 */
public enum AmlType {
    REGULAR(0),
    VOID(1),
    NIL(2),
    MAINT(3);

    private int amlType;


    AmlType(int amlType) {
        this.amlType = amlType;
    }

    @JsonValue
    public Integer getAmlType() {
        return this.amlType;
    }

    private static final Map<Integer, AmlType> amlTypeMap = new HashMap<>();

    static {
        for (AmlType type : AmlType.values()) {
            amlTypeMap.put(type.getAmlType(), type);
        }
    }

    public static AmlType get(Integer amlType) {
        if (!amlTypeMap.containsKey(amlType)) {
            throw  EngineeringManagementServerException.badRequest(ErrorId.INVALID_AML_TYPE_CODE);
        }
        return amlTypeMap.get(amlType);
    }
}
