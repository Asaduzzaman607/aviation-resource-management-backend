package com.digigate.engineeringmanagement.configurationmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "countries")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country extends AbstractDomainBasedEntity {

    private String name;

    private String code;

    private String dialingCode;
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "country", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<City> cities;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Country)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Country country = (Country) o;

        return getId() != null ? getId().equals(country.getId()) : country.getId() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getId() != null ? getId().hashCode() : 0);
        return result;
    }
}
