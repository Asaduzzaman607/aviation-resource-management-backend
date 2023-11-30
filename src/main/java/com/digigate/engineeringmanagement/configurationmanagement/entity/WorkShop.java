package com.digigate.engineeringmanagement.configurationmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Rack;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workshops")
public class WorkShop extends AbstractDomainBasedEntity implements IDto {
    @Column(unique = true)
    String code;
    String address;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;
    @Column(name = "city_id", insertable = false, updatable = false)
    private Long cityId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof WorkShop)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((WorkShop) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
