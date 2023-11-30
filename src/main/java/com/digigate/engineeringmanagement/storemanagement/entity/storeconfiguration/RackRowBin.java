package com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rack_row_bins")
public class RackRowBin extends AbstractDomainBasedEntity {
    @Column(name = "code", nullable = false, length = 100)
    private String code;
    @ManyToOne
    @JoinColumn(name = "rack_row_Id", nullable = false)
    private RackRow rackRow;
    @Column(name = "rack_row_Id", updatable = false, insertable = false)
    private Long rackRowId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof RackRowBin)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((RackRowBin) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
