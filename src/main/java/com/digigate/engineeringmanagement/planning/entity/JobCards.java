package com.digigate.engineeringmanagement.planning.entity;


import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job_cards")
public class JobCards extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_package_id", nullable = false)
    private WorkPackage workPackage;

    @Column(name = "work_package_id", insertable = false, updatable = false)
    private Long workPackageId;

    @Column(nullable = false)
    private String jobCategory;

    private Integer total;
    private Integer completed;
    private Integer deferred;
    private Integer withDrawn;
    private String remark;
}
