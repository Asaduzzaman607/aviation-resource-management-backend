package com.digigate.engineeringmanagement.storemanagement.entity.storedemand;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ReturnPartsDetail;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_return_parts")
public class StoreReturnPart extends AbstractDomainBasedEntity {
    @Column(length = 8000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;      /** This is the Removed Part.*/
    @Column(name = "part_id", insertable = false, updatable = false)
    private Long partId;    /** This is the Removed Part Id.*/

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installed_part_id")
    private Part installedPart;
    @Column(name = "installed_part_id", insertable = false, updatable = false)
    private Long installedPartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "install_part_uom_id", nullable = false)
    private UnitMeasurement installedPartUom;
    @Column(name = "install_part_uom_id", insertable = false, updatable = false)
    private Long installPartUomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remove_part_uom_id", nullable = false)
    private UnitMeasurement removedPartUom;
    @Column(name = "remove_part_uom_id", insertable = false, updatable = false)
    private Long removedPartUomId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_id")
    private StoreReturn storeReturn;
    @Column(name = "return_id", insertable = false, updatable = false)
    private Long storeReturnId;

    @Column(name = "quantity_return")
    private Long quantityReturn;

    @Column(name = "card_line_no", length = 100)
    private String cardLineNo;

    @Column(name = "release_no", length = 100)
    private String releaseNo;

    @Column(name = "is_inactive")
    private Boolean isInactive = Boolean.FALSE; /** This is the Field for part status coming from planning.*/

    @Column(name = "return_id", insertable = false, updatable = false)
    private Long returnUnusablePartId;

    @Transient
    private String remarks;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof StoreReturnPart)) return false;
        return Objects.nonNull(this.getId()) && Objects.equals(this.getId(), (((StoreReturnPart) object).getId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }

}
