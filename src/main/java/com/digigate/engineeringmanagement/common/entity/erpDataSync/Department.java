package com.digigate.engineeringmanagement.common.entity.erpDataSync;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "departments")
public class Department extends AbstractDomainBasedEntity implements Serializable {

    @Column(name = "company_id")
    private String companyId;
    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private String code;
    @Column(name = "info")
    private String info;
    @Column(name = "erp_id")
    private Long erpId;

    public static Department withId(Long id) {
        Department department = new Department();
        department.setId(id);
        return department;
    }
}
