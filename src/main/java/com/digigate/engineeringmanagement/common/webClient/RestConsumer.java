package com.digigate.engineeringmanagement.common.webClient;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.erp.DepartmentSyncDto;
import com.digigate.engineeringmanagement.common.payload.request.erp.DesignationSyncDto;
import com.digigate.engineeringmanagement.common.payload.request.erp.EmployeeSyncDto;
import com.digigate.engineeringmanagement.common.payload.request.erp.SectionSyncDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.STATIC_ERP_ID;

@Component
public class RestConsumer {

    @Value("${erp.base.url}")
    private String erpBaseUrl;
    @Value("${erp.department.url}")
    private String erpDepartmentUrl;
    @Value("${erp.section.url}")
    private String erpSectionUrl;
    @Value("${erp.designation.url}")
    private String erpDesignationUrl;
    @Value("${erp.employee.url}")
    private String erpEmployeeUrl;

    private final ObjectMapper mapper;

    @Autowired
    public RestConsumer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public DepartmentSyncDto getDepartmentsAsJson(String lastSyncTime) {

        WebClient client = WebClient.create();

        WebClient.ResponseSpec responseSpec = client.get()
            .uri(erpBaseUrl + String.format(erpDepartmentUrl, STATIC_ERP_ID, lastSyncTime))
            .retrieve();
        String responseBody = responseSpec.bodyToMono(String.class).block();
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            return mapper.readValue(jsonObject.toString(), DepartmentSyncDto.class);
        } catch (Exception e) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.SYSTEM_ERROR);
        }
    }

    public SectionSyncDto getSectionsAsJson(String lastSyncTime) {
        WebClient client = WebClient.create();
        WebClient.ResponseSpec responseSpec = client.get()
            .uri(erpBaseUrl + String.format(erpSectionUrl, STATIC_ERP_ID, STATIC_ERP_ID, lastSyncTime))
            .retrieve();
        String responseBody = responseSpec.bodyToMono(String.class).block();
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            return mapper.readValue(jsonObject.toString(), SectionSyncDto.class);
        } catch (Exception e) {
            throw new EngineeringManagementServerException(
                ErrorId.UNABLE_TO_RETRIEVE_ERP_DATA,
                HttpStatus.NOT_FOUND,
                MDC.get(ApplicationConstant.TRACE_ID
                ));
        }
    }

    public DesignationSyncDto getDesignationsAsJson(String lastSyncTime) {
        WebClient client = WebClient.create();
        WebClient.ResponseSpec responseSpec = client.get()
            .uri(erpBaseUrl + String.format(erpDesignationUrl, STATIC_ERP_ID, STATIC_ERP_ID, STATIC_ERP_ID, lastSyncTime))
            .retrieve();
        String responseBody = responseSpec.bodyToMono(String.class).block();
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            return mapper.readValue(jsonObject.toString(), DesignationSyncDto.class);
        } catch (Exception e) {
            throw new EngineeringManagementServerException(
                ErrorId.UNABLE_TO_RETRIEVE_ERP_DATA,
                HttpStatus.NOT_FOUND,
                MDC.get(ApplicationConstant.TRACE_ID
                ));
        }
    }

    public EmployeeSyncDto getEmployeesAsJson(String lastSyncTime) {
        WebClient client = WebClient.create();
        WebClient.ResponseSpec responseSpec = client.get()
            .uri(erpBaseUrl + String.format(erpEmployeeUrl, STATIC_ERP_ID, lastSyncTime))
            .retrieve();
        String responseBody = responseSpec.bodyToMono(String.class).block();
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            return mapper.readValue(jsonObject.toString(), EmployeeSyncDto.class);
        } catch (Exception e) {
            throw new EngineeringManagementServerException(
                ErrorId.UNABLE_TO_RETRIEVE_ERP_DATA,
                HttpStatus.NOT_FOUND,
                MDC.get(ApplicationConstant.TRACE_ID
                ));
        }
    }

}
