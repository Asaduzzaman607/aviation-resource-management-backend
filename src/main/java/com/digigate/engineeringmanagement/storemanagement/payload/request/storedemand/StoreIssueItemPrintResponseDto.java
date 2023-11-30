package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;


import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import lombok.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.EMPTY_STRING;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreIssueItemPrintResponseDto {

    private Long id;
    private Long demandItemId;
    private Long issueId;
    @Min(value = 0)
    @Size(max = 100)
    private Integer issuedQuantity = 0;
    private String cardLineNo;
    private PriorityType priorityType;
    @NotNull
    @Min(value = 0)
    private Integer quantityDemanded = 0;
    private Boolean isActive;
    private Long storeDemandId;

    private Long partId;
    private String partNo;
    private PartClassification partClassification;
    private String partDescription;
    private Integer availablePart;
    @Size(max = 8000)
    private String remark = EMPTY_STRING;
    private Long demandedUomId;
    @Size(max = 200)
    private String demandedUomCode;

    //issued uom
    private Long unitMeasurementId;
    private String unitMeasurementCode;

    private String grnNo;
    private Integer quantity = 1;
    private Long serialId;
    private Long issueItemId;
    private String serialNo;
    private Double price;
}
