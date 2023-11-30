package com.digigate.engineeringmanagement.procurementmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "comparative_statement_part_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CsPartDetail extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cs_id", nullable = false)
    private ComparativeStatement comparativeStatement;
    @Column(name = "cs_id", insertable = false, updatable = false)
    private Long comparativeStatementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iq_item_id", nullable = false)
    private VendorQuotationInvoiceDetail iqItem;
    @Column(name = "iq_item_id", insertable = false, updatable = false)
    private Long iqItemId;

    private String moqRemark;

    @OneToMany(
            mappedBy = "csPartDetail",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true)
    private List<CsAuditDisposal> csAuditDisposalSet;

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
