package com.digigate.engineeringmanagement.common.payload.response.erpDataSyncResponseModel.employee;

import com.digigate.engineeringmanagement.configurationmanagement.dto.response.IdNameResponse;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDto {
    private Long id;
    private Long erpId;
    private String name;
    private String code;
    private String presentAddress;
    private String fatherName;
    private String motherName;
    private String nationalId;
    private String passport;
    private String activationCode;
    private String email;
    private String officePhone;
    private String officeMobile;
    private String permanentAddress;
    private String residentPhone;
    private String residentMobile;
    private String bloodGroup;
    private IdNameResponse designation;
    private IdNameResponse section;
    private DepartmentResponseDto department;
}
