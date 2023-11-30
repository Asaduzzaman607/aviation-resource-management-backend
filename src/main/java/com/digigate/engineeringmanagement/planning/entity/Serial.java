package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;

/**
 * Serial entity
 *
 * @author ashinisingha
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "serials",uniqueConstraints={
        @UniqueConstraint(columnNames = {"part_id","serial_number"})})
public class Serial extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(name = "part_id", insertable = false, updatable = false)
    private Long partId;

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Serial serial = (Serial) o;
        return Objects.equals(getPartId(), serial.getPartId()) && Objects.equals(getSerialNumber(), serial.getSerialNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPartId(), getSerialNumber());
    }
}
