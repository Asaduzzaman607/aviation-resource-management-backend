package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Aircraft Interruptions Entity
 *
 * @author Nafiul Islam
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "aircraft_interruptions")
public class AircraftInterruptions extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private AircraftLocation aircraftLocation;

    @Column(name = "location_id", insertable = false, updatable = false, nullable = false)
    private Long locationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false, nullable = false)
    private Long aircraftId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String amlPageNo;

    @Column(nullable = false)
    private String seqNo;

    @Column(name = "defect_description")
    private String defectDescription;

    @Column(name = "rect_description")
    private String rectDescription;

    private Double duration;

    public Optional<String> getDefectDescription() {
        return Optional.ofNullable(defectDescription);
    }

    public Optional<String> getRectDescription() {
        return Optional.ofNullable(rectDescription);
    }

    public Optional<Double> getDuration() {
        return Optional.ofNullable(duration);
    }


}
