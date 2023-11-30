package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VqdQuantityDto {
    @NotEmpty
    @Valid
    private Set<VqdQuantity> vqdQuantities;
    @NotNull
    private Long vqId;
    @NotNull
    private Long poId;
}
