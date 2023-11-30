package com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "rack_rows")
public class RackRow extends AbstractDomainBasedEntity {
    @Column(name = "code", nullable = false, length = 100)
    private String code;
    @ManyToOne
    @JoinColumn(name = "rack_id", nullable = false)
    private Rack rack;
    @Column(name = "rack_id", insertable = false, updatable = false)
    private Long rackId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof RackRow)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((RackRow) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }

}
