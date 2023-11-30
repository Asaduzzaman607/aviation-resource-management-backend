package com.digigate.engineeringmanagement.planning.constant;


import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public enum TaskEffectivity {
    ALL(1),
    NA(2);

    private final Integer id;

    TaskEffectivity(Integer id) {
        this.id = id;
    }

    @JsonValue
    public Integer getId() {
        return this.id;
    }

    private static final Map<Integer, TaskEffectivity> map = new HashMap<>();

    static {
        for( TaskEffectivity taskEffectivity : TaskEffectivity.values() ){
            map.put(taskEffectivity.getId(), taskEffectivity);
        }
    }

    @JsonCreator
    public static TaskEffectivity create( Integer id) {
        if (!map.containsKey(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.TASK_EFFECTIVITY_IS_NOT_VALID,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
        return map.get(id);
    }
}
