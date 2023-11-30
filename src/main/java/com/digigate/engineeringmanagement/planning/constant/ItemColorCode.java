package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum ItemColorCode {

    GREEN(0),
    AMBER(1),
    RED(2);

    public final Integer id;

    ItemColorCode(Integer id) {
        this.id = id;
    }


    @JsonValue
    public Integer getId() {
        return this.id;
    }

    private static final Map<Integer, ItemColorCode> itemColorCodeMap = new HashMap<>();

    static {
        for (ItemColorCode d : ItemColorCode.values()) {
            itemColorCodeMap.put(d.getId(), d);
        }
    }

    public static ItemColorCode get(Integer id) {
        if (!itemColorCodeMap.containsKey(id)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_DUE_DATE_COLOR);
        }
        return itemColorCodeMap.get(id);
    }
}
