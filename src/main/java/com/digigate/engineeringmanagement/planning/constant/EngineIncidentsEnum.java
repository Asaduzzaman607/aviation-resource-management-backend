package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 *  Engine Incidents Enum
 *
 * @author Nafiul Islam
 */
public enum EngineIncidentsEnum {

    ENGINE_IN_FLIGHT_SHUT_DOWNS(0),
    ENGINES_UNSCHEDULED_REMOVALS(1);

    private final Integer engineIncidentsType;

    EngineIncidentsEnum(Integer engineIncidentsType) {
        this.engineIncidentsType = engineIncidentsType;
    }


    @JsonValue
    public Integer getEngineIncidentsType() {
        return this.engineIncidentsType;
    }

    private static final Map<Integer, EngineIncidentsEnum> engineIncidentsEnumHashedMap = new HashedMap<>();

    static {
        for(EngineIncidentsEnum engineIncidentsEnum: EngineIncidentsEnum.values()){
            engineIncidentsEnumHashedMap.put(engineIncidentsEnum.getEngineIncidentsType(), engineIncidentsEnum);
        }
    }

    @JsonCreator
    public static EngineIncidentsEnum create(Integer id){
        if(!engineIncidentsEnumHashedMap.containsKey(id)){
            throw  EngineeringManagementServerException.badRequest(ErrorId.ENGINE_INCIDENTS_ENUM_ERROR);
        }
        return engineIncidentsEnumHashedMap.get(id);
    }

    public static EngineIncidentsEnum getByName(String name){
        for(EngineIncidentsEnum engineIncidentsEnum: EngineIncidentsEnum.values()){
            if(StringUtils.equals(engineIncidentsEnum.name(), name)){
                return engineIncidentsEnum;
            }
        }
        return null;
    }
}
