package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum MelCategory {
    A(0),
    B(1),
    C(2),
    D(3);

    private final Integer id;

    MelCategory(Integer id) {
        this.id = id;
    }


    @JsonValue
    public Integer getId() {
        return id;
    }

    private static final Map<Integer, MelCategory> melCategoryMap = new HashMap<>();

    static {
        for (MelCategory c : MelCategory.values()) {
            melCategoryMap.put(c.getId(), c);
        }
    }

    public static MelCategory get(Integer id) {
        if (!melCategoryMap.containsKey(id)) {
            throw  EngineeringManagementServerException.badRequest(ErrorId.INVALID_MEL_CATEGORY_CODE);
        }
        return melCategoryMap.get(id);
    }
}
