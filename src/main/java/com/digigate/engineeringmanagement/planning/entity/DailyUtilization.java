package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "aml_daily_utilization", uniqueConstraints = {@UniqueConstraint(columnNames = {"aircraft_id", "date"})})
@Builder
public class DailyUtilization extends AbstractDomainBasedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false)
    private Long aircraftId;

    @Column(nullable = false)
    private LocalDate date;

    private Double usedHours;
    private Integer usedCycle;
    private Double tat;
    private Integer tac;

    private Double eng1OilUplift;
    private Double eng2OilUplift;

    private Double apuUsedHrs;
    private Integer apuUsedCycle;

}
