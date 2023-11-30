package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.constant.CheckType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * AircraftCheckDoneList Entity
 *
 * @author Nafiul Islam
 */

@Entity
@Table(name = "a_check_done_list")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AircraftCheckDone extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id",nullable = false)
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long aircraftId;

    @Column(name = "a_check_done_hour")
    private Double aircraftCheckDoneHour;

    @Column(name = "a_check_done_date")
    private LocalDate aircraftCheckDoneDate;

    @Column(nullable = false)
    private CheckType checkType;

}
