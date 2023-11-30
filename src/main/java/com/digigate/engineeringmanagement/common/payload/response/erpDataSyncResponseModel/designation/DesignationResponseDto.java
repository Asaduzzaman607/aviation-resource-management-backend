package com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.designation;

import com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.employee.DepartmentResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.IdNameResponse;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DesignationResponseDto {
    private Long id;
    private Long erpId;
    private String name;
    private IdNameResponse section;
    private DepartmentResponseDto department;
}
