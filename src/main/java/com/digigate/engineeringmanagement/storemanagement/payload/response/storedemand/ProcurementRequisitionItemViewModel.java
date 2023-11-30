package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.digigate.engineeringmanagement.planning.payload.response.PartWiseUomResponseDto;
import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ProcurementRequisitionItemViewModel {
    private Long demandItemId;
    private Integer quantityDemanded;
    private Integer quantityIssued;
    private String cardLineNo;
    private String grnNo;
    private Boolean isActive;
    private Long storeDemandId;

    private Long unitMeasurementId;
    private String unitMeasurementCode;

    private Long partId;
    private String partNo;
    private String partDescription;
    private Integer availablePart;
    private Long vendorId;
    private String vendorName;

    private String serialNos;
    @JsonIgnore
    private Map<Long, String> parentWiseRemarks;

    private Long id;
    private Long requisitionId;
    private Integer requisitionQuantity;
    private PriorityType requisitionPriority;
    private String remark;
    private String department;
    private String ipcCmm;
    private String aircraftName;

}
