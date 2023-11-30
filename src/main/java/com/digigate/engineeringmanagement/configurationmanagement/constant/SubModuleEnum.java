package com.digigate.engineeringmanagement.configurationmanagement.constant;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public enum SubModuleEnum {
    PARTS_DEMAND(4000L, ModuleEnum.STORE),
    PARTS_ISSUE(4001L, ModuleEnum.STORE),
    STORE_CONFIGURATION(4002L, ModuleEnum.STORE),
    PARTS_RETURN(4003L, ModuleEnum.STORE),
    PARTS_REQUISITION(4004L, ModuleEnum.STORE),
    UNSERVICEABLE_ITEM(4005L, ModuleEnum.STORE),
    SCRAP_PARTS(4006L, ModuleEnum.STORE),
    STORE_PARTS_AVAILABILITY(4007L, ModuleEnum.STORE),

    MATERIAL_MANAGEMENT_QUOTE_REQUEST(4008L, ModuleEnum.MATERIAL_MANAGEMENT),
    CONFIGURATION_MATERIAL_MANAGEMENT(4009L, ModuleEnum.MATERIAL_MANAGEMENT),
    MATERIAL_MANAGEMENT_ORDER(4010L, ModuleEnum.MATERIAL_MANAGEMENT),
    MATERIAL_MANAGEMENT_COMPARATIVE_STATEMENT(4011L, ModuleEnum.MATERIAL_MANAGEMENT),

    AIRCRAFT(4012L, ModuleEnum.PLANNING),
    CONFIGURATIONS(4013L, ModuleEnum.PLANNING),
    AIRCRAFT_TECHNICAL_LOG(4014L, ModuleEnum.PLANNING),
    SCHEDULE_TASKS(4015L, ModuleEnum.PLANNING),
    CHECK(4017L, ModuleEnum.PLANNING),

    CONFIGURATION(4018L, ModuleEnum.CONFIGURATION),
    ADMINISTRATION(4019L, ModuleEnum.CONFIGURATION),

    QUALITY_MANUFACTURER(4020L, ModuleEnum.QUALITY),
    QUALITY_SUPPLIER(4021L, ModuleEnum.QUALITY),

    STORE_INSPECTOR(4022L, ModuleEnum.STORE_INSPECTOR),
    INSPECTION_CHECKLIST(4023L, ModuleEnum.STORE_INSPECTOR),

    AIRCRAFT_INFORMATION(4024L, ModuleEnum.CONFIGURATION),
    STORE_WORK_ORDER(4028L, ModuleEnum.STORE),
    MATERIAL_MANAGEMENT_PARTS_INVOICE(4029L, ModuleEnum.MATERIAL_MANAGEMENT),
    QUALITY_SHIPMENT_PROVIDER(4030L, ModuleEnum.QUALITY),
    CONFIGURATION_MANUFACTURER(4031L, ModuleEnum.CONFIGURATION),
    MATERIAL_MANAGEMENT_SUPPLIER(4032L, ModuleEnum.MATERIAL_MANAGEMENT),
    MATERIAL_MANAGEMENT_SHIPMENT_PROVIDER(4033L, ModuleEnum.MATERIAL_MANAGEMENT),
    QUALITY_INSPECTION_CHECKLIST(4034L, ModuleEnum.QUALITY),
    ENGINE_PROPELLER_LANDING_GEAR(4035L, ModuleEnum.PLANNING),
    OTHERS(4036L, ModuleEnum.PLANNING),
    PLANNING_FOLDERS(4037L, ModuleEnum.PLANNING),
    DASHBOARD(4038L, ModuleEnum.PLANNING),
    SETTINGS(4049L,ModuleEnum.PLANNING),
    LOGISTIC_QUOTE_REQUEST(4039L, ModuleEnum.LOGISTIC),
    LOGISTIC_ORDER(4040L, ModuleEnum.LOGISTIC),
    LOGISTIC_COMPARATIVE_STATEMENT(4041L, ModuleEnum.LOGISTIC),
    LOGISTIC_PARTS_INVOICE(4042L, ModuleEnum.LOGISTIC),
    MATERIAL_MANAGEMENT_PARTS_INVOICE_AUDIT(4043L, ModuleEnum.AUDIT),
    MATERIAL_MANAGEMENT_PARTS_INVOICE_FINANCE(4044L, ModuleEnum.FINANCE),
    LOGISTIC_PARTS_INVOICE_AUDIT(4045L, ModuleEnum.AUDIT),
    LOGISTIC_PARTS_INVOICE_FINANCE(4046L, ModuleEnum.FINANCE),

    RESOURCE_MANAGEMENT_SUBMODULE(4047L, ModuleEnum.RESOURCE_MANAGEMENT),

    PARTS_RECEIVE(4048L, ModuleEnum.FRS),
    DUTY_FEES(4050L,ModuleEnum.LOGISTIC),
    LOGISTIC_TRACKER(4051L, ModuleEnum.LOGISTIC);

    private static final Map<Long, SubModuleEnum> ConfigSubModuleMap = new HashMap<>();

    static {
        for (SubModuleEnum csm : SubModuleEnum.values()) {
            ConfigSubModuleMap.put(csm.getSubModuleId(), csm);
        }
    }

    private final Long subModuleId;
    private final ModuleEnum moduleEnum;

    SubModuleEnum(Long subModuleId, ModuleEnum moduleEnum) {
        this.subModuleId = subModuleId;
        this.moduleEnum = moduleEnum;
    }

    public static SubModuleEnum byId(Integer id) {
        if (!ConfigSubModuleMap.containsKey(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.DATA_NOT_FOUND,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return ConfigSubModuleMap.get(id);
    }

    @JsonValue
    public Long getSubModuleId() {
        return this.subModuleId;
    }

    @JsonValue
    public ModuleEnum getModule() {
        return this.moduleEnum;
    }
}
