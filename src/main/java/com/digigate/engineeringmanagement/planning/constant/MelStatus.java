package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum MelStatus {
    OPEN(1),
    CLOSE(2);

    private final Integer id;


    MelStatus(Integer id) {
        this.id = id;
    }

    @JsonValue
    public Integer getId() {
        return id;
    }


    private static final Map<Integer, MelStatus> melStatusMap = new HashMap<>();

    static {
        for (MelStatus m : MelStatus.values()) {
            melStatusMap.put(m.getId(), m);
        }
    }

    public static MelStatus get(Integer id) {
        if (!melStatusMap.containsKey(id)) {
            throw  EngineeringManagementServerException.badRequest(ErrorId.INVALID_MEL_STATUS_CODE);
        }
        return melStatusMap.get(id);
    }
}
