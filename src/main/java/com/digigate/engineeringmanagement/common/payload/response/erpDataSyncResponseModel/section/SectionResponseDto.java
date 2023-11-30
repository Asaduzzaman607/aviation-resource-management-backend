package com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.section;

import com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.employee.DepartmentResponseDto;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SectionResponseDto {
    private Long id;
    private Long erpId;
    private String name;
    private DepartmentResponseDto department;
}
