package com.digigate.engineeringmanagement.procurementmanagement.dto.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequisitionPoProjection {
    private Long id;
    private String requisitionNo;
    private Long poId;
}
