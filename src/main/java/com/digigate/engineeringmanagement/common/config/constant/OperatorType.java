package com.digigate.engineeringmanagement.common.config.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.collections4.map.HashedMap;

import java.util.Map;

public enum OperatorType {
    EQUAL("equal"),
    NOT_EQUAL("notEqual"),
    LESS_THAN("lessThan"),
    LESS_THAN_OR_EQUAL("lessThanOrEqual"),
    GREATER_THAN("greaterThan"),
    GREATER_THAN_OR_EQUAL("greaterThanOrEqual");

    private final String type;

    OperatorType(String type){
        this.type = type;
    }

    @JsonValue
    public String getType(){
        return this.type;
    }

    private static final Map<String, OperatorType> map = new HashedMap<>();

    static {
        for(OperatorType operatorType: OperatorType.values()){
            map.put(operatorType.getType(), operatorType);
        }
    }

    @JsonCreator
    public static OperatorType create(String id){
        if(!map.containsKey(id)){
            throw EngineeringManagementServerException.badRequest(ErrorId.FAILED_TO_PARSE_OPERATOR_TYPE_ENUM);
        }
        return map.get(id);
    }

}
