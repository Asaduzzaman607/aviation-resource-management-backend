package com.digigate.engineeringmanagement.planning.entity;


import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import com.digigate.engineeringmanagement.planning.constant.EngineIncidentsEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Engine Incidents  Entity
 *
 * @author Nafiul Islam
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "engine_incidents")
public class EngineIncidents extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_model_id", nullable = false)
    private AircraftModel aircraftModel;

    @Column(name = "aircraft_model_id", insertable = false, updatable = false)
    private Long aircraftModelId;

    @Column(nullable = false)
    private EngineIncidentsEnum engineIncidentsEnum;

    @Column(nullable = false)
    private LocalDate date;
}
