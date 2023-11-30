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
@Table(name = "designations")
public class Designation extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private Section section;
    @Column(name = "section_id", insertable = false, updatable = false)
    private Long sectionId;
    @Column(name = "name")
    private String name;
    @Column(name = "erp_id")
    private Long erpId;

    public static Designation withId(Long designationId) {
        Designation designation = new Designation();
        designation.setId(designationId);
        return designation;
    }
}
