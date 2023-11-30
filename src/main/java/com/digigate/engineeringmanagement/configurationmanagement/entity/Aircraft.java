package com.digigate.engineeringmanagement.configurationmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "aircrafts")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Aircraft extends AbstractDomainBasedEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String aircraftName;

    @Column(name = "airframe_serial", nullable = false, unique = true)
    private String airframeSerial;

    @Column(
            name = "airframe_total_time",
            columnDefinition = "decimal(10,2) default '0.00'",
            nullable = false
    )
    private Double airFrameTotalTime = 0.0;

    @Column(
            name = "bd_total_time",
            columnDefinition = "decimal(10,2) default '0.00'",
            nullable = false
    )
    private Double bdTotalTime = 0.0;

    @Column(
            name = "airframe_total_cycle",
            columnDefinition = "integer default 0",
            nullable = false
    )
    private Integer airframeTotalCycle = 0;

    @Column(
            name = "bd_total_cycle",
            columnDefinition = "integer default 0",
            nullable = false
    )
    private Integer bdTotalCycle = 0;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "daily_average_cycle", nullable = false)
    private Integer dailyAverageCycle;

    @Column(name = "daily_average_hours", nullable = false)
    private Double dailyAverageHours;

    @Column(name = "daily_average_apu_cycle", nullable = false)
    private Integer dailyAverageApuCycle;

    @Column(name = "daily_average_apu_hours", nullable = false)
    private Double dailyAverageApuHours;

    @Column(name = "total_apu_cycle", nullable = false)
    private Integer totalApuCycle;

    @Column(name = "total_apu_hours", nullable = false)
    private Double totalApuHours;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aircraft_model_id", nullable = false)
    private AircraftModel aircraftModel;

    @Column(name = "aircraft_model_id", insertable = false, updatable = false, nullable = false)
    private Long aircraftModelId;

    @Column(name = "a_check_done_hour")
    private Double aircraftCheckDoneHour;

    @Column(name = "engine_type")
    private String engineType;

    @Column(name = "propeller_type")
    private String propellerType;

    @Column(name = "a_check_done_date")
    private LocalDate aircraftCheckDoneDate;

    @CreationTimestamp
    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(name = "induction_date")
    private LocalDate inductionDate;


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AircraftModel)) return false;
        return this.getId() != 0 && this.getId().equals(((AircraftModel) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
