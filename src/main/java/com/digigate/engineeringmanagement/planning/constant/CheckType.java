package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public enum CheckType {


    A("A"),
    C("C"),
    TWO_YEAR("2Y"),
    FOUR_YEAR("4Y"),
    EIGHT_YEAR("8Y");

    public final String val;


    CheckType(String val) {
        this.val = val;
    }

    private static final Map<String, CheckType> checkTypeMap = new HashMap<>();

    @JsonValue
    public String getVal() {
        return this.val;
    }

    static {
        for (CheckType d : CheckType.values()) {
            checkTypeMap.put(d.getVal(), d);
        }
    }


    public static CheckType getVal(String val) {
        if (!checkTypeMap.containsKey(val)) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_CHECK_TYPE, HttpStatus.NOT_FOUND, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
        return checkTypeMap.get(val);
    }
}
