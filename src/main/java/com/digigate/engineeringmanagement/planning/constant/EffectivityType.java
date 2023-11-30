package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum EffectivityType {

    NON_EFFECTIVE(0),
    EFFECTIVE(1);

    private final Integer id;

    EffectivityType(Integer id) {
        this.id = id;
    }

    @JsonValue
    public Integer getId() {
        return this.id;
    }

    private static final Map<Integer, EffectivityType> effectivityTypeMap = new HashMap<>();

    static {
        for (EffectivityType d : EffectivityType.values()) {
            effectivityTypeMap.put(d.getId(), d);
        }
    }

    public static EffectivityType get(Integer id) {
        if (!effectivityTypeMap.containsKey(id)) {
            throw  EngineeringManagementServerException.badRequest(ErrorId.INVALID_EFFECTIVITY_TYPE);
        }
        return effectivityTypeMap.get(id);
    }

    public static EffectivityType getByName(String name){
        for(EffectivityType effectivityType : EffectivityType.values()){
            if(StringUtils.equals(effectivityType.name(), name)){
                return effectivityType;
            }
        }
        return null;
    }
}
