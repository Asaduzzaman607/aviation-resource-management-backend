package com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.City;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "offices")
public class Office extends AbstractDomainBasedEntity {
    @Column(name = "code", unique = true, nullable = false, length = 100)
    private String code;
    @Column(name = "address", length = 8000)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location locations;

    @Column(name = "location_id", insertable = false, updatable = false)
    private Long locationId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Office)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((Office) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }

}
