package com.digigate.engineeringmanagement.common.entity.erpDataSync;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "employees")
public class Employee extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id")
    private Designation designation;
    @Column(name = "designation_id", insertable = false, updatable = false)
    private Long designationId;
    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private String code;
    @Column(name = "present_address")
    private String presentAddress;
    @Column(name = "father_name")
    private String fatherName;
    @Column(name = "mother_name")
    private String motherName;
    @Column(name = "national_id")
    private String nationalId;
    @Column(name = "passport")
    private String passport;
    @Column(name = "activation_code")
    private String activationCode;
    @Column(name = "email")
    private String email;
    @Column(name = "office_phone")
    private String officePhone;
    @Column(name = "office_mobile")
    private String officeMobile;
    @Column(name = "permanent_address")
    private String permanentAddress;
    @Column(name = "resident_phone")
    private String residentPhone;
    @Column(name = "resident_mobile")
    private String residentMobile;
    @Column(name = "blood_group")
    private String bloodGroup;
    @Column(name = "erp_id")
    private Long erpId;

    @JsonIgnore
    public static Employee withId(Long id) {
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }
}
