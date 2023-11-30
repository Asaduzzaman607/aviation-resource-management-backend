package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * AircraftCheckIndex entity
 *
 * @author Ashraful
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "aircraft_check_indexes")
public class AircraftCheckIndex extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false, nullable = false)
    private Long aircraftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wo_id")
    private WorkOrder workOrder;

    @Column(name = "wo_id", insertable = false, updatable = false)
    private Long woId;

    private Date doneDate;

    private Double doneHour;

    private Integer doneCycle;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "aircraft_checks",
            joinColumns = {@JoinColumn(name = "ac_check_index_id")},
            inverseJoinColumns = {@JoinColumn(name = "ac_type_check_id")}
    )
    private Set<AircraftCheck> aircraftTypeCheckSet;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "ac_index_ldnd",
            joinColumns = {@JoinColumn(name = "ac_check_index_id")},
            inverseJoinColumns = {@JoinColumn(name = "ldnd_id")}
    )
    private Set<Ldnd> ldndSet;
}
