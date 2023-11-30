package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.constant.DefectType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Defect  Entity
 *
 * @author Asifur Rahman
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "defects")
public class Defect extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long aircraftId;

    @ManyToOne
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(name = "part_id", insertable = false, updatable = false)
    private Long partId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "location_id")
    private AircraftLocation aircraftLocation;

    @Column(name = "location_id", insertable = false, updatable = false)
    private Long locationId;

    @Column(nullable = false)
    private LocalDate date;

    private String reference;

    @Column(length = 500)
    private String defectDesc;

    @Column(length = 500)
    private String actionDesc;
    private DefectType defectType;


    public Optional<Long> getPartId() {
        return Optional.ofNullable(partId);
    }

    public Optional<Long> getLocationId() {
        return Optional.ofNullable(locationId);
    }

    public Optional<String> getReference() {
        return Optional.ofNullable(reference);
    }

    public Optional<String> getDefectDesc() {
        return Optional.ofNullable(defectDesc);
    }

    public Optional<String> getActionDesc() {
        return Optional.ofNullable(actionDesc);
    }

    public Optional<DefectType> getDefectType() {
        return Optional.ofNullable(defectType);
    }
}
