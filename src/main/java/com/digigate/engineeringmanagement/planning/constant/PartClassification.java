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

public enum PartClassification {

    ROTABLE(1),
    CONSUMABLE(2),
    EXPENDABLE(3);

    private final Integer id;


    PartClassification(Integer id) {
        this.id = id;
    }


    @JsonValue
    public Integer getId(){ return this.id;}

    private static final Map<Integer, PartClassification> partClassificationMap = new HashMap<>();

    static {
        for(PartClassification partClassification : PartClassification.values()){
            partClassificationMap.put(partClassification.getId(), partClassification);
        }
    }

    @JsonCreator
    public static PartClassification create(Integer id){
        if(!partClassificationMap.containsKey(id)){
            throw new EngineeringManagementServerException(
                    ErrorId.DATA_NOT_FOUND,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
        return partClassificationMap.get(id);
    }

    public static PartClassification getByName(String name){
        for(PartClassification partClassification : PartClassification.values()){
            if(StringUtils.equals(partClassification.name(), name)){
                return partClassification;
            }
        }
        return null;
    }

}
