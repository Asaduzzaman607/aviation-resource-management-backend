package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.constant.ClassificationTypeEnum;
import com.digigate.engineeringmanagement.configurationmanagement.constant.IncidentTypeEnum;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Aircraft Incidents Entity
 *
 * @author Nafiul Islam
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "aircraft_incidents")
public class AircraftIncidents extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false, nullable = false)
    private Long aircraftId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "incident_type", nullable = false)
    private IncidentTypeEnum incidentTypeEnum;

    @Column(name = "classification", nullable = false)
    private ClassificationTypeEnum classificationTypeEnum;

    @Column(nullable = false, length = 500)
    private String incidentDesc;

    @Column(nullable = false, length = 500)
    private String actionDesc;

    @Column(nullable = false)
    private String referenceAtl;

    @Column(nullable = false)
    private String seqNo;

    private String remarks;
}
