package com.digigate.engineeringmanagement.procurementmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "comparative_statement_details")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CsDetail extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cs_id", nullable = false)
    private ComparativeStatement comparativeStatement;
    @Column(name = "cs_id", insertable = false, updatable = false)
    private Long comparativeStatementId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id", nullable = false)
    private VendorQuotation vendorQuotation;
    @Column(name = "quote_id", insertable = false, updatable = false)
    private Long vendorQuotationId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof CsDetail)) return false;
        return Objects.nonNull(this.getId()) && Objects
                .equals(this.getId(), (((CsDetail) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
