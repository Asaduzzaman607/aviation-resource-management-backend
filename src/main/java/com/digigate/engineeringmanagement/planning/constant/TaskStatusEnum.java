package com.digigate.engineeringmanagement.planning.constant;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum TaskStatusEnum {
    OPEN(0),
    CLOSED(1),
    REP(2);

    private Integer taskStatusType;

    TaskStatusEnum(int taskStatusType) {
        this.taskStatusType = taskStatusType;
    }

    public Integer getTaskStatusType() {
        return taskStatusType;
    }

    private static final Map<Integer, TaskStatusEnum> taskStatusEnumMap = new HashMap<>();

    static {
        for (TaskStatusEnum m : TaskStatusEnum.values()) {
            taskStatusEnumMap.put(m.getTaskStatusType(), m);
        }
    }

    public static TaskStatusEnum get(Integer id) {
        if (!taskStatusEnumMap.containsKey(id)) {
            throw  EngineeringManagementServerException.badRequest(ErrorId.INVALID_TASK_STATUS_TYPE);
        }
        return taskStatusEnumMap.get(id);
    }

    public static TaskStatusEnum getByName(String name){
        for(TaskStatusEnum taskStatusEnum : TaskStatusEnum.values()){
            if(StringUtils.equals(taskStatusEnum.name(), name)){
                return taskStatusEnum;
            }
        }
        return null;
    }
}
