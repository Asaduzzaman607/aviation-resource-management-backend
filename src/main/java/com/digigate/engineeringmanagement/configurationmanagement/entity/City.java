package com.digigate.engineeringmanagement.configurationmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cities")
public class City extends AbstractDomainBasedEntity {

    @NotNull
    private String name;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;
    @Column(name = "country_id", insertable = false, updatable = false)
    private Long countryId;
    @Column(name = "zip_code")
    private String zipCode;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof City)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((City) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }

}
