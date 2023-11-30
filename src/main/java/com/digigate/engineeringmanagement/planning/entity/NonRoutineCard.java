package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Non Routine Card Entity
 *
 * @author ashinisingha
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "non_routine_cards")
public class NonRoutineCard extends AbstractDomainBasedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long aircraftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ac_check_index_id")
    private AircraftCheckIndex aircraftCheckIndex;

    @Column(name = "ac_check_index_id", insertable = false, updatable = false)
    private Long acCheckIndexId;

    @Column(unique = true, nullable = false)
    private String nrcNo;

    private String reference;

    @Column(nullable = false)
    private LocalDate issueDate;
}
