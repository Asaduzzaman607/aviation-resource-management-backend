
package com.digigate.engineeringmanagement.common.payload.request.erp;

import lombok.Data;

@Data
public class EmployeeDataDto {
    private Long id;
    private String activationCode;
    private String bloodGroup;
    private String code;
    private Long designationId;
    private String email;
    private String fatherName;
    private String motherName;
    private String name;
    private String nationalId;
    private String officeMobile;
    private String officePhone;
    private String passport;
    private String permanentAddress;
    private String presentAddress;
    private String residentMobile;
    private String residentPhone;
}
