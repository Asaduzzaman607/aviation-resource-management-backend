package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VqdQuantity {
    @NotNull
    private Long id;
    @NotNull
    private Integer quantity;
    private Boolean isDiscount = Boolean.FALSE;
    private String vendorSerials;
}
