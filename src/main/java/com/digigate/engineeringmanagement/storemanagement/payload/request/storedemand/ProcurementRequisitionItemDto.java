package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.procurementmanagement.constant.ExchangeType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ProcurementRequisitionItemDto {
    private Long id;
    @Size(max = 8000)
    private String remark;
    private Long demandItemId;
    @NotNull
    @Min(value = 0)
    private Integer quantityRequested;
    private PriorityType priorityType;

    @JsonIgnore
    private StoreDemandItem storeDemandItem;
    @JsonIgnore
    private ProcurementRequisition requisition;
    @JsonIgnore
    @Builder.Default
    private InputType inputType = InputType.CS;
}
