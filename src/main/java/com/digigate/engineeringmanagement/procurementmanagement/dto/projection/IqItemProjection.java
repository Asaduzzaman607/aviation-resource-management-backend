package com.digigate.engineeringmanagement.procurementmanagement.dto.projection;

import com.digigate.engineeringmanagement.storemanagement.constant.PriorityType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IqItemProjection {
    private Long id;
    private Integer partQuantity;
    private String vendorSerials;
    private Integer reqQuantity;
    private Long uomId;
    private String uomCode;
    private Long partId;
    private String partNo;
    private String partDescription;
    private Long partUomId;
    private String partUomCode;
    private Long altPartId;
    private String altPartNo;
    private String altPartDescription;
    private Long altPartUomId;
    private String altPartUomCode;
    private Long demandItemId;
    private PriorityType priorityType;
    private Double unitPrice;
    private String condition;
    private String leadTime;
    private Long currencyId;
    private String currencyCode;

    public IqItemProjection(Long id,
                            Integer partQuantity,
                            String vendorSerials,
                            Integer reqQuantity,
                            Long uomId,
                            String uomCode,
                            Long partId,
                            String partNo,
                            String partDescription,
                            Long partUomId,
                            String partUomCode,
                            Long altPartId,
                            String altPartNo,
                            String altPartDescription,
                            Long altPartUomId,
                            String altPartUomCode,
                            Long demandItemId,
                            PriorityType priorityType) {
        this.id = id;
        this.partQuantity = partQuantity;
        this.vendorSerials = vendorSerials;
        this.reqQuantity = reqQuantity;
        this.uomId = uomId;
        this.uomCode = uomCode;
        this.partId = partId;
        this.partNo = partNo;
        this.partDescription = partDescription;
        this.partUomId = partUomId;
        this.partUomCode = partUomCode;
        this.altPartId = altPartId;
        this.altPartNo = altPartNo;
        this.altPartDescription = altPartDescription;
        this.altPartUomId = altPartUomId;
        this.altPartUomCode = altPartUomCode;
        this.demandItemId = demandItemId;
        this.priorityType = priorityType;
    }
}
