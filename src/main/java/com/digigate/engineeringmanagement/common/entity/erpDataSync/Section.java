package com.digigate.engineeringmanagement.common.entity.erpDataSync;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "sections")
public class Section extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    @Column(name = "department_id", insertable = false, updatable = false)
    private Long departmentId;
    @Column(name = "name")
    private String name;
    @Column(name = "erp_id")
    private Long erpId;

    public static Section withId(Long sectionId) {
        Section section = new Section();
        section.setId(sectionId);
        return section;
    }
}
