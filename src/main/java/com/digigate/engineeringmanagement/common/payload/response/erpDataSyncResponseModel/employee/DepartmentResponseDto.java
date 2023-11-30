package com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.employee;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DepartmentResponseDto {
    private Long id;
    private String companyId;
    private String name;
    private String code;
    private String info;
    private Long erpId;
}
