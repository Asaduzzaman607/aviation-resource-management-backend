package com.digigate.engineeringmanagement.procurementmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.entity.User;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "comparative_statement_audit_disposals")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CsAuditDisposal extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cs_part_detail_id", nullable = false)
    private CsPartDetail csPartDetail;
    @Column(name = "cs_part_detail_id", insertable = false, updatable = false)
    private Long csPartDetailId;
    private String auditDisposal;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by_id", nullable = false)
    private User submittedBy;
    @Column(name = "submitted_by_id", insertable = false, updatable = false)
    private Long submittedById;

    public CsPartDetail getCsPartDetailWithId(Long id){
        CsPartDetail csPartDetailWithId = new CsPartDetail();
        csPartDetailWithId.setId(id);
        return csPartDetailWithId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof CsPartDetail)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((CsPartDetail) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
