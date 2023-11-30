package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DashboardPartAvailabilityViewModel {
    private Long id;
    private String partNo;
    private Integer quantity;
    private Integer demandQuantity;
    private Integer issuedQuantity;
    private Integer requisitionQuantity;
    private String uom;
    private String store;
    private Integer minStock;
    private Integer maxStock;
}
