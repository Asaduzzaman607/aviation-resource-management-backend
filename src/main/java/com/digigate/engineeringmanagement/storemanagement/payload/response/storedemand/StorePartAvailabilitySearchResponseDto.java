package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import lombok.*;

import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorePartAvailabilitySearchResponseDto {
    private Long id;
    private Long partId;
    private String partNo;
    private Long officeId;
    private String officeCode;
    private Integer quantity;
    private Integer demandQuantity;
    private Integer issuedQuantity;
    private Integer requisitionQuantity;
    private Integer uomWiseQuantity;
    private Long uomId;
    @Size(max = 200)
    private String uomCode;
    private Integer minStock;
    private Integer maxStock;
}
