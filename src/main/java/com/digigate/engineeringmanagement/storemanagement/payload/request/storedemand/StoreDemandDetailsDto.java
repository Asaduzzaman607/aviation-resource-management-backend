package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.EMPTY_STRING;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreDemandDetailsDto implements IDto {
    private Long id;
    @NotNull
    @Min(value = 0)
    private Integer quantityDemanded = 0;
    private Integer totalIssuedQty;
    private PriorityType priorityType;
    private Boolean isActive;
    private Long storeDemandId;
    @Size(max = 8000)
    private String ipcCmm;
    private Long partId;
    private String partNo;
    private PartClassification partClassification;
    private String partDescription;
    private Integer availablePart;
    @Size(max = 8000)
    private String remark = EMPTY_STRING;
    @JsonIgnore
    private Map<Long, String> parentWiseRemarks;

    private Long parentPartId;
    private Long unitMeasurementId;
    private String unitMeasurementCode;
    private String department;
    private String airCraftName;
    private List<AlterPartDto> alterPartDtoList;
}
