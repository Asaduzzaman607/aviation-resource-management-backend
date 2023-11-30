package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.planning.constant.OilRecordTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

/**
 * AML Oil Record entity
 *
 * @author Sayem Hasnat
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "aml_oil_records")
public class AmlOilRecord extends AbstractDomainBasedEntity {
    @Column(name = "type", nullable = false)
    private OilRecordTypeEnum type;
    private Double hydOil1;
    private Double hydOil2;
    private Double hydOil3;
    private Double engineOil1;
    private Double engineOil2;
    private Double apuOil;
    private Double csdOil1;
    private Double csdOil2;
    private Double oilRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aml_id")
    private AircraftMaintenanceLog aircraftMaintenanceLog;

    @Column(name = "aml_id", insertable = false, updatable = false)
    private Long amlId;

    public AmlOilRecord(Long amlId, Double engineOil1, Double engineOil2, Double apuOil) {
        this.amlId = amlId;
        this.engineOil1 = engineOil1;
        this.engineOil2 = engineOil2;
        this.apuOil = apuOil;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AmlOilRecord)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((AmlOilRecord) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
