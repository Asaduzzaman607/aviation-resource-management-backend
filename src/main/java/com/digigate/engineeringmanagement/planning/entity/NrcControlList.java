package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

/**
 *Nrc ControlList Entity
 *
 * @author ashinisingha
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "nrc_control_list")
public class NrcControlList extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ac_check_index_id", nullable = false)
    private AircraftCheckIndex aircraftCheckIndex;
    @Column(name = "ac_check_index_id", insertable = false, updatable = false)
    private Long aircraftCheckIndexId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;
    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long aircraftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wo_id")
    private WorkOrder workOrder;
    @Column(name = "wo_id", insertable = false, updatable = false)
    private Long woId;

    private LocalDate date;

}
