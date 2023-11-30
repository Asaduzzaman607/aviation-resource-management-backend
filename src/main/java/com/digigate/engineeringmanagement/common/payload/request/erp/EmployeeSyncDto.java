
package com.digigate.engineeringmanagement.common.payload.request.erp;

import lombok.Data;

import java.util.List;

@Data
public class EmployeeSyncDto {
    private Long companyId;
    private List<EmployeeDataDto> employees;
}
