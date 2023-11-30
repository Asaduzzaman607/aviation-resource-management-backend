package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;
import lombok.*;

import javax.persistence.*;

/**
 * Signature entity
 *
 * @author ashinisingha
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "signatures")
public class Signature extends AbstractDomainBasedEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    @Column(name = "employee_id", updatable = false, insertable = false)
    private Long employeeId;
    @Column(unique = true, nullable = false)
    private String authNo;
}
