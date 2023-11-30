package com.digigate.engineeringmanagement.logistic.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "duty_fee_items")
public class DutyFeeItem extends AbstractDomainBasedEntity {

    @Column(name = "fees")
    private String fees;
    @Column(name = "total_amount")
    private Double totalAmount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "duty_fee_id")
    private DutyFee dutyFee;
    @Column(name = "duty_fee_id", insertable = false, updatable = false)
    private Long dutyFeeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency;
    @Column(name = "currency_id", insertable = false, updatable = false)
    private Long currencyId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof DutyFeeItem)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((DutyFeeItem) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }

}
