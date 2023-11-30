package com.digigate.engineeringmanagement.storemanagement.entity.storedemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.planning.entity.Serial;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.EMPTY_STRING;
import static com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType.ISSUE;
import static com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType.SCRAP;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_parts_serials")
public class StorePartSerial extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avail_id", nullable = false)
    private StorePartAvailability storePartAvailability;
    @Column(name = "avail_id", insertable = false, updatable = false)
    private Long storePartAvailabilityId;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "serial_id")
    private Serial serial;
    @Column(name = "serial_id", updatable = false, insertable = false)
    private Long serialId;

    @Column(columnDefinition = "Decimal(10,2) default '0.00'")
    private Double price = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency;
    @Column(name = "currency_id", insertable = false, updatable = false)
    private Long currencyId;

    @Column(name = "rack_life")
    private LocalDate rackLife;

    @Column(name = "self_life")
    private LocalDate selfLife;

    @Column(name = "grn_no")
    private String grnNo;

    @Column(name = "quantity", columnDefinition = "integer default 0")
    private Integer quantity = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "part_status")
    private PartStatus partStatus;

    @Column(name = "shelf_life_type")
    private String shelfLifeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "parent_type")
    private StorePartAvailabilityLogParentType parentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uomId", nullable = false)
    private UnitMeasurement unitMeasurement;
    @Column(name = "uomId", insertable = false, updatable = false)
    private Long uomId;

    public String getSerialNumber() {
        Serial serial = getSerial();
        return Objects.isNull(serial) ? EMPTY_STRING : serial.getSerialNumber();
    }

    public String getCurrencyCode(){
        Currency currency = getCurrency();
        return Objects.isNull(currency) ? EMPTY_STRING : currency.getCode();
    }

    public boolean existsInStore() {
        return this.getParentType() != ISSUE && this.getParentType() != SCRAP;
    }

    public boolean notExistsInStore() {
        return !existsInStore();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StorePartSerial that = (StorePartSerial) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId());
    }
}
