package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public enum PartHierarchyType {
    PART(0),
    HIGHER_PART(1);
    private static final Map<Integer, PartHierarchyType> taxModeMap = new HashMap<>();

    static {
        for (PartHierarchyType partHierarchyType : PartHierarchyType.values()) {
            taxModeMap.put(partHierarchyType.getType(), partHierarchyType);
        }
    }

    private Integer type;

    PartHierarchyType(Integer type) {
        this.type = type;
    }

    @JsonValue
    public Integer getType() {
        return this.type;
    }

    public static PartHierarchyType getNameFromValue(Integer mode) {
        return taxModeMap.get(mode);
    }

    @JsonCreator
    public static PartHierarchyType create(Integer type) {
        if (!taxModeMap.containsKey(type)) {
            throw new EngineeringManagementServerException(ErrorId.INVALID_PART_HIERARCHY_TYPE,
                    HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
        }
        return taxModeMap.get(type);
    }

    public static PartHierarchyType getByName(String name){
        for(PartHierarchyType partHierarchyType: PartHierarchyType.values()){
            if(StringUtils.equals(partHierarchyType.name(), name)){
                return partHierarchyType;
            }
        }
        return null;
    }

}