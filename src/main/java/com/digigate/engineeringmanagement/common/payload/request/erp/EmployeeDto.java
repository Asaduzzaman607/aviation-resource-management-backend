package com.digigate.engineeringmanagement.common.payload.request.erp;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EmployeeDto implements IDto{
    private Long id;
    private Long designationId;
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

}
