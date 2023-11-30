package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.EMPTY_STRING;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreIssueItemResponseDto {

    private Long id;
    private Long demandItemId;
    private Long issueId;
    @Min(value = 0)
    @Size(max = 100)
    private Integer issuedQuantity = 0;
    private Integer totalIssuedQty;
    private String cardLineNo;
    private List<GrnAndSerialDto> grnAndSerialDtoList = new ArrayList<>();
    private PriorityType priorityType;
    @NotNull
    @Min(value = 0)
    private Integer quantityDemanded = 0;
    private Boolean isActive;
    private Long storeDemandId;

    private Long partId;
    private Long parentPartId;
    private String partNo;
    private PartClassification partClassification;
    private String partDescription;
    private Integer availablePart;
    @Size(max = 8000)
    private String remark = EMPTY_STRING;
    private Long unitMeasurementId;
    private String unitMeasurementCode;

    //extracted GrnAndSerialDto fields
    private String grnNo;
    private Integer quantity = 1;
    private Long serialId;
    private Long issueItemId;
    private String serialNo;
    private Double price;
}
