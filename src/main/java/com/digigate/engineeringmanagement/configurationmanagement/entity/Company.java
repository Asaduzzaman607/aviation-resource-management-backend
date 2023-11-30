package com.digigate.engineeringmanagement.configurationmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.constant.Currencies;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "companies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Company extends AbstractDomainBasedEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String companyName;

    @Column(name = "address_line_1")
    private String addressLineOne;

    @Column(name = "address_line_2")
    private String addressLineTwo;

    @Column(name = "address_line_3")
    private String addressLineThree;

    @Column(name = "phone")
    private String phone;

    @Column(name = "fax")
    private String fax;

    @Column(name = "email")
    private String email;

    @Column(name = "contact_person")
    private String contactPerson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_currency_id", nullable = false)
    private Currency baseCurrency;
    @Column(name = "base_currency_id", updatable = false, insertable = false)
    private Long baseCurrencyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_currency_id", nullable = false)
    private Currency localCurrency;
    @Column(name = "local_currency_id", updatable = false, insertable = false)
    private Long localCurrencyId;

    @Column(name = "shipment_address_1")
    private String shipmentAddressOne;

    @Column(name = "shipment_address_2")
    private String shipmentAddressTwo;

    @Column(name = "shipment_address_3")
    private String shipmentAddressThree;

    @Column(name = "company_url")
    private String companyUrl;

    @Column(name = "company_logo")
    private String companyLogo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(name = "city_id", insertable = false, updatable = false)
    private Long cityId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Company)) return false;
        return this.getId() != 0 && this.getId().equals(((Company) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
