package com.digigate.engineeringmanagement.configurationmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "aircraft_models")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftModel extends AbstractDomainBasedEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String aircraftModelName;

    @Column(name = "description", columnDefinition = "TEXT")
    private  String description;

    @Column(name = "a_check_hour")
    private Double checkHourForA;

    @Column(name = "a_check_days")
    private Integer checkDaysForA;

    @Column(name = "c_check_hour")
    private Double checkHourForC;

    @Column(name = "c_check_days")
    private Integer checkDaysForC;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AircraftModel)) return false;
        return this.getId() != 0 && this.getId().equals(((AircraftModel) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
