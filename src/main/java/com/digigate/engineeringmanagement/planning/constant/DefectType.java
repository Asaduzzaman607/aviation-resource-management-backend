package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum DefectType {
    PIREP(0),
    MAREP(1);


    private final Integer id;

    DefectType(Integer id) {
        this.id = id;
    }

    @JsonValue
    public Integer getId() {
        return this.id;
    }

    private static final Map<Integer, DefectType> defectTypeMap = new HashMap<>();

    static {
        for (DefectType d : DefectType.values()) {
            defectTypeMap.put(d.getId(), d);
        }
    }

    public static DefectType get(Integer id) {
        if (!defectTypeMap.containsKey(id)) {
            throw  EngineeringManagementServerException.badRequest(ErrorId.INVALID_DEFECT_TYPE);
        }
        return defectTypeMap.get(id);
    }

    public static DefectType getByName(String name){
        for(DefectType defectType : DefectType.values()){
            if(StringUtils.equals(defectType.name(), name)){
                return defectType;
            }
        }
        return null;
    }
}
