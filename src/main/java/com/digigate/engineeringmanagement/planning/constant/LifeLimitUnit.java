package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public enum LifeLimitUnit {
    FH(0),
    FC(1),
    AH(2),
    AC(3),
    DY(4);

    private final Integer id;

    LifeLimitUnit(Integer id) {
        this.id = id;
    }

    @JsonValue
    public Integer getId() {
        return this.id;
    }

    private static final Map<Integer, LifeLimitUnit> lifeLimitUnitMap = new HashedMap<>();

    static {
        for(LifeLimitUnit lifeLimitUnit: LifeLimitUnit.values()){
            lifeLimitUnitMap.put(lifeLimitUnit.getId(), lifeLimitUnit);
        }
    }

    @JsonCreator
    public static LifeLimitUnit create(Integer id){
        if(!lifeLimitUnitMap.containsKey(id)){
            throw  EngineeringManagementServerException.badRequest(ErrorId.LIFE_LIMIT_UNIT_ENUM_ERROR);
        }
        return lifeLimitUnitMap.get(id);
    }

    public static LifeLimitUnit getByName(String name){
        for(LifeLimitUnit lifeLimitUnit: LifeLimitUnit.values()){
            if(StringUtils.equals(lifeLimitUnit.name(), name)){
                return lifeLimitUnit;
            }
        }
        return null;
    }

}
