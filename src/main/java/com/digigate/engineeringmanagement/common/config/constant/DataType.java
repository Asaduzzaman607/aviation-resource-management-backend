package com.digigate.engineeringmanagement.common.config.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.collections4.map.HashedMap;

import java.util.Map;

public enum DataType {
    DOUBLE("double"),
    LONG("long"),
    INT("int"),
    DATE("date"),
    STRING("string"),
    DATE_TIME("dateTime"),
    LIST("list"),
    PART_CLASSIFICATION("partClassification"),
    PART_UNIT_OF_MEASURE("partUnitOfMeasure"),
    MODEL_TYPE("modelType"),
    LIFE_CODES("lifeCodes"),
    LIFE_LIMIT_UNIT("lifeLimitUnit"),
    BOOLEAN("boolean"),
    EFFECTIVITY_TYPE("effectivityType"),
    REPETITIVE_TYPE("repetitiveType"),
    INTERVAL_TYPE("intervalType"),
    TASK_STATUS("taskStatus");

    private final String type;

    DataType(String type){
        this.type = type;
    }

    @JsonValue
    public String getType(){
        return this.type;
    }

    private static final Map<String, DataType> map = new HashedMap<>();

    static {
        for(DataType dataType: DataType.values()){
            map.put(dataType.getType(), dataType);
        }
    }

    @JsonCreator
    public static DataType create(String id){
        if(!map.containsKey(id)){
            throw EngineeringManagementServerException.badRequest(ErrorId.FAILED_TO_PARSE_DATA_TYPE_ENUM);
        }
        return map.get(id);
    }

}
