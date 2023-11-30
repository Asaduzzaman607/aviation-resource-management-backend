package com.digigate.engineeringmanagement.storemanagement.entity.partsdemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_parts_loans_items")
public class StorePartLoanDetails extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_part_loan_id")
    private StorePartLoan storePartLoan;
    @Column(name = "store_part_loan_id", insertable = false, updatable = false)
    private Long storePartLoanId;

    @Column(name = "store_part_serial_id")
    private Long storePartSerialId;

    @Transient
    private String remarks;
    
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StorePartLoanDetails)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((StorePartLoanDetails) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
