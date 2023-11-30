package com.digigate.engineeringmanagement.configurationmanagement.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Incident Type Enum
 *
 * @author Nafiul Islam
 */
public enum IncidentTypeEnum {

    TECHNICAL_INCIDENTS(0),
    NON_TECHNICAL_INCIDENTS(1);

    private final Integer incidentType;

    IncidentTypeEnum(Integer incidentType) {
        this.incidentType = incidentType;
    }
    @JsonValue
    public Integer getIncidentType() {
        return this.incidentType;
    }

    private static final Map<Integer, IncidentTypeEnum> incidentTypeEnumHashedMap = new HashedMap<>();

    static {
        for(IncidentTypeEnum incidentTypeEnum: IncidentTypeEnum.values()){
            incidentTypeEnumHashedMap.put(incidentTypeEnum.getIncidentType(), incidentTypeEnum);
        }
    }

    @JsonCreator
    public static IncidentTypeEnum create(Integer id){
        if(!incidentTypeEnumHashedMap.containsKey(id)){
            throw  EngineeringManagementServerException.badRequest(ErrorId.INCIDENT_TYPE_ENUM_ERROR);
        }
        return incidentTypeEnumHashedMap.get(id);
    }

    public static IncidentTypeEnum getByName(String name){
        for(IncidentTypeEnum incidentTypeEnum: IncidentTypeEnum.values()){
            if(StringUtils.equals(incidentTypeEnum.name(), name)){
                return incidentTypeEnum;
            }
        }
        return null;
    }
}
