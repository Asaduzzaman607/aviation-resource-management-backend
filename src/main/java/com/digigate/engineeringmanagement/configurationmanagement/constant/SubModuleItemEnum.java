package com.digigate.engineeringmanagement.configurationmanagement.constant;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.fasterxml.jackson.annotation.JsonValue;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public enum SubModuleItemEnum {

    STORE_DEMAND(50000L, SubModuleEnum.PARTS_DEMAND),
    PENDING_DEMAND(50001L, SubModuleEnum.PARTS_DEMAND),
    APPROVED_DEMAND(50002L, SubModuleEnum.PARTS_DEMAND),
    DEMAND_REPORT(50003L, SubModuleEnum.PARTS_DEMAND),

    ISSUE_DEMAND(50004L, SubModuleEnum.PARTS_ISSUE),
    PENDING_ISSUE(50005L, SubModuleEnum.PARTS_ISSUE),
    APPROVED_ISSUE(50006L, SubModuleEnum.PARTS_ISSUE),
    ISSUE_REPORT(50007L, SubModuleEnum.PARTS_ISSUE),

    TECHNICAL_STORE(50008L, SubModuleEnum.STORE_CONFIGURATION),
    ROOM(50009L, SubModuleEnum.STORE_CONFIGURATION),
    RACK(50010L, SubModuleEnum.STORE_CONFIGURATION),
    RACK_ROW(50011L, SubModuleEnum.STORE_CONFIGURATION),
    RACK_ROW_BIN(50012L, SubModuleEnum.STORE_CONFIGURATION),
    STOCK_ROOM(50013L, SubModuleEnum.STORE_CONFIGURATION),
    UNIT_OF_MEASUREMENT(50014L, SubModuleEnum.CONFIGURATION),

    PART_RETURN(50015L, SubModuleEnum.PARTS_RETURN),
    PENDING_PARTS_RETURN(50016L, SubModuleEnum.PARTS_RETURN),
    APPROVED_PARTS_RETURN(50017L, SubModuleEnum.PARTS_RETURN),

    MATERIAL_MANAGEMENT_REQUISITION(50018L, SubModuleEnum.PARTS_REQUISITION),
    PENDING_MANAGEMENT_REQUISITION(50019L, SubModuleEnum.PARTS_REQUISITION),
    APPROVED_MANAGEMENT_REQUISITION(50020L, SubModuleEnum.PARTS_REQUISITION),
    MATERIAL_MANAGEMENT_REQUISITION_REPORT(50021L, SubModuleEnum.PARTS_REQUISITION),

    UNSERVICEABLE_ITEM(50022L, SubModuleEnum.UNSERVICEABLE_ITEM),

    SCRAP_PART(50023L, SubModuleEnum.SCRAP_PARTS),

    STORE_PARTS(50024L, SubModuleEnum.STORE_PARTS_AVAILABILITY),
    PARTS_AVAILABILITY(50025L, SubModuleEnum.STORE_PARTS_AVAILABILITY),

    MATERIAL_MANAGEMENT_REQUEST_FOR_QUOTATION(50026L, SubModuleEnum.MATERIAL_MANAGEMENT_QUOTE_REQUEST),
    MATERIAL_MANAGEMENT_PENDING_RFQ(50027L, SubModuleEnum.MATERIAL_MANAGEMENT_QUOTE_REQUEST),
    MATERIAL_MANAGEMENT_APPROVED_RFQ(50028L, SubModuleEnum.MATERIAL_MANAGEMENT_QUOTE_REQUEST),
    MATERIAL_MANAGEMENT_QUOTATION(50029L, SubModuleEnum.MATERIAL_MANAGEMENT_QUOTE_REQUEST),

    MATERIAL_MANAGEMENT_SHIPMENT_PROVIDER(50030L, SubModuleEnum.MATERIAL_MANAGEMENT_SHIPMENT_PROVIDER),
    MATERIAL_MANAGEMENT_SUPPLIER(50031L, SubModuleEnum.MATERIAL_MANAGEMENT_SUPPLIER),

    MATERIAL_MANAGEMENT_PURCHASE_ORDER(50032L, SubModuleEnum.MATERIAL_MANAGEMENT_ORDER),

    MATERIAL_MANAGEMENT_GENERATE_CS(50033L, SubModuleEnum.MATERIAL_MANAGEMENT_COMPARATIVE_STATEMENT),
    MATERIAL_MANAGEMENT_PENDING_CS(50034L, SubModuleEnum.MATERIAL_MANAGEMENT_COMPARATIVE_STATEMENT),
    MATERIAL_MANAGEMENT_APPROVED_CS(50035L, SubModuleEnum.MATERIAL_MANAGEMENT_COMPARATIVE_STATEMENT),

    AIRCRAFT(50036L, SubModuleEnum.AIRCRAFT),
    SEATING_CONFIGURATION(50037L, SubModuleEnum.AIRCRAFT),
    AIRCRAFT_LOCATION(50038L, SubModuleEnum.AIRCRAFT),
    POSITION(50039L, SubModuleEnum.AIRCRAFT),
    MODEL(50040L, SubModuleEnum.AIRCRAFT),
    PARTS(50041L, SubModuleEnum.AIRCRAFT),
    MODEL_TREES(50042L, SubModuleEnum.AIRCRAFT),
    BUILD_AIRCRAFT(50043L, SubModuleEnum.AIRCRAFT),
    AC_CHECK_DONE(50128L, SubModuleEnum.AIRCRAFT),
    SERIAL(50129L, SubModuleEnum.AIRCRAFT),

    AC_TYPE(50044L, SubModuleEnum.CONFIGURATIONS),
    AIRPORT(50045L, SubModuleEnum.CONFIGURATIONS),
    CABIN_SEAT_TYPE(50046L, SubModuleEnum.CONFIGURATIONS),

    ATL(50047L, SubModuleEnum.AIRCRAFT_TECHNICAL_LOG),
    SIGNATURES(50048L, SubModuleEnum.AIRCRAFT_TECHNICAL_LOG),
    //    ADD_MEL_CDL_STATUS(50049L, SubModuleEnum.AIRCRAFT_TECHNICAL_LOG),
    ATL_BOOKS(50050L, SubModuleEnum.AIRCRAFT_TECHNICAL_LOG),
    AIRCRAFT_MEL_CDL_STATUS(50066L, SubModuleEnum.AIRCRAFT_TECHNICAL_LOG),
    SECTOR_WISE_UTILIZATION(50079L, SubModuleEnum.AIRCRAFT_TECHNICAL_LOG),
    DAILY_FLYING_HOURS_AND_CYCLE(50130L, SubModuleEnum.AIRCRAFT_TECHNICAL_LOG),
    DAILY_UTILIZATION_RECORD(50131L, SubModuleEnum.AIRCRAFT_TECHNICAL_LOG),
    OIL_AND_FUEL_UPLIFT_RECORD(50132L, SubModuleEnum.AIRCRAFT_TECHNICAL_LOG),
    NON_ROUTINE_CARD(50133L, SubModuleEnum.AIRCRAFT_TECHNICAL_LOG),
    MAINTENANCE_DEFECT_REGISTER(50134L, SubModuleEnum.AIRCRAFT_TECHNICAL_LOG),
    MONTHLY_UTILIZATION_RECORD(50186L, SubModuleEnum.AIRCRAFT_TECHNICAL_LOG),
    YEARLY_UTILIZATION_RECORD(50187L, SubModuleEnum.AIRCRAFT_TECHNICAL_LOG),


    TASK_RECORDS(50051L, SubModuleEnum.SCHEDULE_TASKS),
    TASK_DONE(50052L, SubModuleEnum.SCHEDULE_TASKS),
    TASK_FORECASTS(50053L, SubModuleEnum.SCHEDULE_TASKS),
    AIRCRAFT_TASK(50054L, SubModuleEnum.SCHEDULE_TASKS),
    CONSUMABLE_PARTS(50055L, SubModuleEnum.SCHEDULE_TASKS),
    MAINTENANCE_WORK_ORDERS(50056L, SubModuleEnum.SCHEDULE_TASKS),
    TASK_TYPE(50057L, SubModuleEnum.SCHEDULE_TASKS),
    AIRCRAFT_HARD_TIME_COMPONENT_STATUS(50062L, SubModuleEnum.SCHEDULE_TASKS),
    AMP_STATUS(50064L, SubModuleEnum.SCHEDULE_TASKS),
    SERVICE_BULLETIN_LIST(50073L, SubModuleEnum.SCHEDULE_TASKS),
    AIRFRAME_AND_APPLIANCE_AD_STATUS(50135L, SubModuleEnum.SCHEDULE_TASKS),
    STC_AND_MOD_STATUS(50136L, SubModuleEnum.SCHEDULE_TASKS),

    ENGINE_LLP_STATUS(50060L, SubModuleEnum.ENGINE_PROPELLER_LANDING_GEAR),
    ENGINE_AD_STATUS(50075L, SubModuleEnum.ENGINE_PROPELLER_LANDING_GEAR),
    PROPELLER_STATUS(50076L, SubModuleEnum.ENGINE_PROPELLER_LANDING_GEAR),
    ENGINE_INFORMATION(50137L,SubModuleEnum.ENGINE_PROPELLER_LANDING_GEAR),
    MAIN_LANDING_GEAR_STATUS(50138L,SubModuleEnum.ENGINE_PROPELLER_LANDING_GEAR),
    NOSE_LANDING_GEAR_STATUS(50139L,SubModuleEnum.ENGINE_PROPELLER_LANDING_GEAR),
    REMOVED_MAIN_LANDING_GEAR_STATUS(50178L,SubModuleEnum.ENGINE_PROPELLER_LANDING_GEAR),
    REMOVED_NOSE_LANDING_GEAR_STATUS(50179L,SubModuleEnum.ENGINE_PROPELLER_LANDING_GEAR),
    REMOVED_ENGINE_LLP_STATUS(50180L, SubModuleEnum.ENGINE_PROPELLER_LANDING_GEAR),

    MAN_HOURS(50065L, SubModuleEnum.CHECK),
    NRC_CONTROL_LIST(50067L, SubModuleEnum.CHECK),
    WORK_PACKAGE_SUMMARY(50068L, SubModuleEnum.CHECK),
    WORK_PACKAGE_CERTIFICATION_RECORD(50069L, SubModuleEnum.CHECK),
    WORK_SCOPE_APPROVAL(50071L, SubModuleEnum.CHECK),


    ON_CONDITION_COMPONENT_LIST(50072L, SubModuleEnum.OTHERS),
    AIRCRAFT_COMPONENT_HISTORY_CARD(50061L, SubModuleEnum.OTHERS),
    APU_LLP_STATUS(50181L,SubModuleEnum.OTHERS),
    REMOVAL_APU_LLP_STATUS(50182L,SubModuleEnum.OTHERS),
    APU_LLP_LAST_SHOP_VISIT_INFO(50183L,SubModuleEnum.OTHERS),

//    ENGINE_LLPS_STATUE(50060L, SubModuleEnum.REPORTS),
//    AIRCRAFT_COMPONENT_HISTORY_CARD(50061L, SubModuleEnum.REPORTS),
//    AIRCRAFT_HARD_TIME_COMPONENT_STATUS(50062L, SubModuleEnum.REPORTS),
//    AMP_STATUS(50064L, SubModuleEnum.REPORTS),
//    MAN_HOURS(50065L, SubModuleEnum.REPORTS),
//    NRC_CONTROL_LIST(50067L, SubModuleEnum.REPORTS),
//    WORK_PACKAGE_SUMMARY(50068L, SubModuleEnum.REPORTS),
//    WORK_PACKAGE_CERTIFICATION_RECORD(50069L, SubModuleEnum.REPORTS),

    //    WORK_SCOPE_APPROVAL(50071L, SubModuleEnum.REPORTS),
//    ON_CONDITION_COMPONENT_LIST(50072L, SubModuleEnum.REPORTS),
//    SERVICE_BULLETIN_LIST(50073L, SubModuleEnum.REPORTS),
//    STC_MOD_STATUS(50074L, SubModuleEnum.REPORTS),
//    //    ENGINE_AD_STATUS(50075L, SubModuleEnum.REPORTS),
////    PROPELLER_STATUS(50076L, SubModuleEnum.REPORTS),
//    AIR_WORTHINESS_DIRECTIVE(50077L, SubModuleEnum.REPORTS),
//    OIL_UPLIFT_REPORTS(50078L, SubModuleEnum.REPORTS),
    //    SECTOR_WISE_UTILIZATION(50079L, SubModuleEnum.REPORTS),

    CHECK(50080L, SubModuleEnum.CHECK),
    AC_CHECKS(50081L, SubModuleEnum.CHECK),
    AC_CHECK_INDEX(50082L, SubModuleEnum.CHECK),

    AMP_REVISION(50184L,SubModuleEnum.SETTINGS),



//    INSPECTION_CONTROL_CARD(50063L, SubModuleEnum.REPORTS),
//    WORK_PACKAGE_TASK_INDEX(50070L, SubModuleEnum.REPORTS),
//    STC_MOD_STATUS(50074L, SubModuleEnum.REPORTS),
//    AIRFRAME_APPLIANCE_AD_STATUS(50058L, SubModuleEnum.REPORTS),
//    DAILY_FLYING_HOURS_CYCLES(50059L, SubModuleEnum.REPORTS),
//    AIR_WORTHINESS_DIRECTIVE(50077L, SubModuleEnum.REPORTS),
//    OIL_UPLIFT_REPORTS(50078L, SubModuleEnum.REPORTS),


    BASE_PLANT(50083L, SubModuleEnum.CONFIGURATION),
    BASE(50084L, SubModuleEnum.CONFIGURATION),
    LOCATION(50085L, SubModuleEnum.CONFIGURATION),
    COMPANY(50086L, SubModuleEnum.CONFIGURATION),
    CONFIGURATION_MANUFACTURE(50087L, SubModuleEnum.CONFIGURATION_MANUFACTURER),
    EXTERNAL_DEPARTMENT(50088L, SubModuleEnum.CONFIGURATION),
    CURRENCY(50089L, SubModuleEnum.CONFIGURATION),
    VENDOR_CAPABILITIES(50090L, SubModuleEnum.CONFIGURATION),

    USERS(50091L, SubModuleEnum.ADMINISTRATION),
    ROLES(50092L, SubModuleEnum.ADMINISTRATION),
    ACCESS_RIGHTS(50093L, SubModuleEnum.ADMINISTRATION),
    MODULE(50094L, SubModuleEnum.ADMINISTRATION),
    SUB_MODULE(50095L, SubModuleEnum.ADMINISTRATION),
    SUB_MODULE_ITEM(50096L, SubModuleEnum.ADMINISTRATION),
    WORKFLOW_ACTIONS(50097L, SubModuleEnum.ADMINISTRATION),
    APPROVAL_SETTINGS(50098L, SubModuleEnum.ADMINISTRATION),
    NOTIFICATION_SETTINGS(50099L, SubModuleEnum.ADMINISTRATION),

    CONFIGURATION_MANUFACTURER_PENDING_LIST(50100L, SubModuleEnum.CONFIGURATION_MANUFACTURER),
    CONFIGURATION_MANUFACTURER_APPROVED_LIST(50101L, SubModuleEnum.CONFIGURATION_MANUFACTURER),

    STORE_INSPECTION(50102L, SubModuleEnum.STORE_INSPECTOR),

    STORE_INSPECTOR_INSPECTION_CHECKLIST(50103L, SubModuleEnum.INSPECTION_CHECKLIST),
    STORE_INSPECTOR_PENDING_INSPECTION_CHECKLIST(50104L, SubModuleEnum.INSPECTION_CHECKLIST),
    STORE_INSPECTOR_APPROVED_INSPECTION_CHECKLIST(50105L, SubModuleEnum.INSPECTION_CHECKLIST),

    MATERIAL_MANAGEMENT_AUDIT_PENDING_CS(50106L, SubModuleEnum.MATERIAL_MANAGEMENT_COMPARATIVE_STATEMENT),
    MATERIAL_MANAGEMENT_AUDIT_APPROVED_CS(50107L, SubModuleEnum.MATERIAL_MANAGEMENT_COMPARATIVE_STATEMENT),

    MATERIAL_MANAGEMENT_FINAL_PENDING_CS(50108L, SubModuleEnum.MATERIAL_MANAGEMENT_COMPARATIVE_STATEMENT),
    MATERIAL_MANAGEMENT_FINAL_APPROVED_CS(50109L, SubModuleEnum.MATERIAL_MANAGEMENT_COMPARATIVE_STATEMENT),

    STORE_WORK_ORDER(50110L, SubModuleEnum.STORE_WORK_ORDER),
    PENDING_STORE_WORK_ORDER(50111L, SubModuleEnum.STORE_WORK_ORDER),
    APPROVE_STORE_WORK_ORDER(50112L, SubModuleEnum.STORE_WORK_ORDER),

    MATERIAL_MANAGEMENT_PARTS_INVOICE(50113L, SubModuleEnum.MATERIAL_MANAGEMENT_PARTS_INVOICE),
    PENDING_SCRAP_PART(50114L, SubModuleEnum.SCRAP_PARTS),
    APPROVED_SCRAP_PART(50115L, SubModuleEnum.SCRAP_PARTS),

    MATERIAL_MANAGEMENT_SHIPMENT_PROVIDER_PENDING_LIST(50116L, SubModuleEnum.MATERIAL_MANAGEMENT_SHIPMENT_PROVIDER),
    MATERIAL_MANAGEMENT_SHIPMENT_PROVIDER_APPROVED_LIST(50117L, SubModuleEnum.MATERIAL_MANAGEMENT_SHIPMENT_PROVIDER),

    MATERIAL_MANAGEMENT_SUPPLIER_PENDING_LIST(50118L, SubModuleEnum.MATERIAL_MANAGEMENT_SUPPLIER),
    MATERIAL_MANAGEMENT_SUPPLIER_APPROVED_LIST(50119L, SubModuleEnum.MATERIAL_MANAGEMENT_SUPPLIER),

    QUALITY_MANUFACTURER_PENDING_LIST(50120L, SubModuleEnum.QUALITY_MANUFACTURER),
    QUALITY_MANUFACTURER_APPROVED_LIST(50121L, SubModuleEnum.QUALITY_MANUFACTURER),

    QUALITY_SUPPLIER_PENDING_LIST(50124L, SubModuleEnum.QUALITY_SUPPLIER),
    QUALITY_SUPPLIER_APPROVED_LIST(50125L, SubModuleEnum.QUALITY_SUPPLIER),

    QUALITY_PENDING_INSPECTION_CHECKLIST(50126L, SubModuleEnum.QUALITY_INSPECTION_CHECKLIST),
    QUALITY_APPROVED_INSPECTION_CHECKLIST(50127L, SubModuleEnum.QUALITY_INSPECTION_CHECKLIST),

    FOLDER_ATL(50140L,SubModuleEnum.PLANNING_FOLDERS),
    FOLDER_WORK_ORDER(50141L,SubModuleEnum.PLANNING_FOLDERS),
    FOLDER_AD(50142L,SubModuleEnum.PLANNING_FOLDERS),
    FOLDER_SB(50143L,SubModuleEnum.PLANNING_FOLDERS),
    FOLDER_OUT_OF_PHASE_TASK_CARD(50144L,SubModuleEnum.PLANNING_FOLDERS),
    FOLDER_ARC(50145L,SubModuleEnum.PLANNING_FOLDERS),
    FOLDER_DMI_LOG(50146L,SubModuleEnum.PLANNING_FOLDERS),
    FOLDER_CDL_LOG(50147L,SubModuleEnum.PLANNING_FOLDERS),
    FOLDER_ON_BOARD_DOCUMENTS(50148L,SubModuleEnum.PLANNING_FOLDERS),
    FOLDER_LETTERS(50149L,SubModuleEnum.PLANNING_FOLDERS),
    FOLDER_OTHERS(50150L,SubModuleEnum.PLANNING_FOLDERS),
    FOLDER_TASK_DONE(50185L,SubModuleEnum.PLANNING_FOLDERS),

    DASHBOARD_ITEM(50151L,SubModuleEnum.DASHBOARD),

    LOGISTIC_REQUEST_FOR_QUOTATION(50152L, SubModuleEnum.LOGISTIC_QUOTE_REQUEST),
    LOGISTIC_PENDING_RFQ(50153L, SubModuleEnum.LOGISTIC_QUOTE_REQUEST),
    LOGISTIC_APPROVED_RFQ(50154L, SubModuleEnum.LOGISTIC_QUOTE_REQUEST),
    LOGISTIC_QUOTATION(50155L, SubModuleEnum.LOGISTIC_QUOTE_REQUEST),

    LOGISTIC_GENERATE_CS(50156L, SubModuleEnum.LOGISTIC_COMPARATIVE_STATEMENT),
    LOGISTIC_PENDING_CS(50157L, SubModuleEnum.LOGISTIC_COMPARATIVE_STATEMENT),
    LOGISTIC_APPROVED_CS(50158L, SubModuleEnum.LOGISTIC_COMPARATIVE_STATEMENT),

    LOGISTIC_AUDIT_PENDING_CS(50159L, SubModuleEnum.LOGISTIC_COMPARATIVE_STATEMENT),
    LOGISTIC_AUDIT_APPROVED_CS(50160L, SubModuleEnum.LOGISTIC_COMPARATIVE_STATEMENT),

    LOGISTIC_FINAL_PENDING_CS(50161L, SubModuleEnum.LOGISTIC_COMPARATIVE_STATEMENT),
    LOGISTIC_FINAL_APPROVED_CS(50162L, SubModuleEnum.LOGISTIC_COMPARATIVE_STATEMENT),

    LOGISTIC_PURCHASE_ORDER(50163L, SubModuleEnum.LOGISTIC_ORDER),

    LOGISTIC_PARTS_INVOICE(50164L, SubModuleEnum.LOGISTIC_PARTS_INVOICE),

    MATERIAL_MANAGEMENT_PENDING_PARTS_INVOICE_AUDIT(50165L, SubModuleEnum.MATERIAL_MANAGEMENT_PARTS_INVOICE_AUDIT),
    MATERIAL_MANAGEMENT_APPROVED_PARTS_INVOICE_AUDIT(50166L, SubModuleEnum.MATERIAL_MANAGEMENT_PARTS_INVOICE_AUDIT),

    MATERIAL_MANAGEMENT_PENDING_PARTS_INVOICE_FINANCE(50167L, SubModuleEnum.MATERIAL_MANAGEMENT_PARTS_INVOICE_FINANCE),
    MATERIAL_MANAGEMENT_APPROVED_PARTS_INVOICE_FINANCE(50168L, SubModuleEnum.MATERIAL_MANAGEMENT_PARTS_INVOICE_FINANCE),

    LOGISTIC_PENDING_PARTS_INVOICE_AUDIT(50169L, SubModuleEnum.LOGISTIC_PARTS_INVOICE_AUDIT),
    LOGISTIC_APPROVED_PARTS_INVOICE_AUDIT(50170L, SubModuleEnum.LOGISTIC_PARTS_INVOICE_AUDIT),

    LOGISTIC_PENDING_PARTS_INVOICE_FINANCE(50171L, SubModuleEnum.LOGISTIC_PARTS_INVOICE_FINANCE),
    LOGISTIC_APPROVED_PARTS_INVOICE_FINANCE(50172L, SubModuleEnum.LOGISTIC_PARTS_INVOICE_FINANCE),

    DEPARTMENT(50173L, SubModuleEnum.RESOURCE_MANAGEMENT_SUBMODULE),
    SECTION(50174L, SubModuleEnum.RESOURCE_MANAGEMENT_SUBMODULE),
    DESIGNATION(50175L, SubModuleEnum.RESOURCE_MANAGEMENT_SUBMODULE),
    EMPLOYEE(50176L, SubModuleEnum.RESOURCE_MANAGEMENT_SUBMODULE),
    INWARD(50177L, SubModuleEnum.PARTS_RECEIVE),
    STORE_INSPECTION_GRN(50188L, SubModuleEnum.STORE_INSPECTOR),
    DUTY_FEES(50189L,SubModuleEnum.DUTY_FEES),
    TRACKER(50190L,SubModuleEnum.LOGISTIC_TRACKER);

    private static final Map<Long, SubModuleItemEnum> ConfigSubModuleItemMap = new HashMap<>();

    static {
        for (SubModuleItemEnum csmi : SubModuleItemEnum.values()) {
            ConfigSubModuleItemMap.put(csmi.getSubModuleItemId(), csmi);
        }
    }

    private final Long subModuleItemId;
    private final SubModuleEnum subModuleEnum;

    SubModuleItemEnum(Long subModuleItemId, SubModuleEnum subModuleEnum) {
        this.subModuleEnum = subModuleEnum;
        this.subModuleItemId = subModuleItemId;

    }

    public static SubModuleItemEnum byId(Integer id) {
        if (!ConfigSubModuleItemMap.containsKey(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.DATA_NOT_FOUND,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return ConfigSubModuleItemMap.get(id);
    }

    @JsonValue
    public Long getSubModuleItemId() {
        return this.subModuleItemId;
    }

    @JsonValue
    public SubModuleEnum getSubModule() {
        return this.subModuleEnum;
    }
}