package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum DefermentCode {
    MAN(1),
    MACO(2),
    PART(3),
    TIME(4),
    TOOL(5);

    public final Integer id;

    DefermentCode(Integer id) {
        this.id = id;
    }

    @JsonValue
    public Integer getId() {
        return this.id;
    }

    private static final Map<Integer, DefermentCode> defermentCodeMap = new HashMap<>();

    static {
        for (DefermentCode d : DefermentCode.values()) {
            defermentCodeMap.put(d.getId(), d);
        }
    }

    public static DefermentCode get(Integer id) {
        if (!defermentCodeMap.containsKey(id)) {
            throw  EngineeringManagementServerException.badRequest(ErrorId.INVALID_DEFERMENT_CODE);
        }
        return defermentCodeMap.get(id);
    }
}
