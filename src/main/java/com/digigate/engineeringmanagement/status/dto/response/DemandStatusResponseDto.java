package com.digigate.engineeringmanagement.status.dto.response;

import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DemandStatusResponseDto {
    private Long demandId;
    private String voucherNo;
    private Integer quantity;
    private Long partId;
    private String partNo;
    private String status;
    private VoucherType voucherType;
    private String workflowType;
    private String module;
    private Boolean rejected;
    private Boolean active;
    private InputType inputType;
}
