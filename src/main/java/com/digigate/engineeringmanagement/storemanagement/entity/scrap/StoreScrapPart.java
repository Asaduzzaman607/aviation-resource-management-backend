package com.digigate.engineeringmanagement.storemanagement.entity.scrap;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "store_scrap_parts")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StoreScrapPart extends AbstractDomainBasedEntity {
    @Column(name = "is_alive", columnDefinition="bit default 1", nullable = false)
    private Boolean isAlive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_scrap_id")
    private StoreScrap storeScrap;
    @Column(name = "store_scrap_id", insertable = false, updatable = false)
    private Long storeScrapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;
    @Column(name = "part_id", insertable = false, updatable = false)
    private Long partId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uomId", nullable = false)
    private UnitMeasurement unitMeasurement;
    @Column(name = "uomId", insertable = false, updatable = false)
    private Long uomId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreScrapPart)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((StoreScrapPart) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
