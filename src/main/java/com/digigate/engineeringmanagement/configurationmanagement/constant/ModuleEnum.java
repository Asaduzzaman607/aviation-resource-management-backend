package com.digigate.engineeringmanagement.configurationmanagement.constant;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public enum ModuleEnum {
    STORE(100L),
    PLANNING(300L),
    CONFIGURATION(400L),
    MATERIAL_MANAGEMENT(401L),
    QUALITY(500L),
    STORE_INSPECTOR(600L),
    LOGISTIC(700L),
    AUDIT(800L),
    FINANCE(900L),
    RESOURCE_MANAGEMENT(901L),
    FRS(902L);

    private static final Map<Long, ModuleEnum> ConfigModulesMap = new HashMap<>();

    static {
        for (ModuleEnum cm : ModuleEnum.values()) {
            ConfigModulesMap.put(cm.getConfigModuleId(), cm);
        }
    }

    private final Long configModuleId;

    ModuleEnum(Long configModuleId) {
        this.configModuleId = configModuleId;
    }

    public static ModuleEnum byId(Long id) {
        if (!ConfigModulesMap.containsKey(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.DATA_NOT_FOUND,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return ConfigModulesMap.get(id);
    }

    @JsonValue
    public Long getConfigModuleId() {
        return this.configModuleId;
    }
}
