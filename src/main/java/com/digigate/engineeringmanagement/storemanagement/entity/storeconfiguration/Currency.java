package com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "currencies")
public class Currency extends AbstractDomainBasedEntity {

    @Column(name = "code", unique = true, nullable = false, length = 100)
    private String code;

    @Column(length = 8000)
    private String description;

    public static Currency withId(Long currencyId) {
        if (currencyId == null) {
            return null;
        }
        Currency currency = new Currency();
        currency.setId(currencyId);
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Currency)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Currency currency = (Currency) o;

        return getId() != null ? getId().equals(currency.getId()) : currency.getId() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }

}
