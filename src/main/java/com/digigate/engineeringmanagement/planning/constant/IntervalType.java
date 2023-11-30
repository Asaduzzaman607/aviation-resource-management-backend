package com.digigate.engineeringmanagement.planning.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public enum IntervalType {
    THRESHOLD(0),
    INTERVAL(1);

    private final Integer id;

    IntervalType(Integer id) {
        this.id = id;
    }

    @JsonValue
    public Integer getId() {
        return this.id;
    }

    private static final Map<Integer, IntervalType> intervalTypeMap = new HashedMap<>();

    static {
        for (IntervalType type : IntervalType.values()) {
            intervalTypeMap.put(type.getId(), type);
        }
    }

    public static IntervalType getByName(String name) {
        for (IntervalType intervalType : IntervalType.values()) {
            if (StringUtils.equals(intervalType.name(), name)) {
                return intervalType;
            }
        }
        return null;
    }
}
