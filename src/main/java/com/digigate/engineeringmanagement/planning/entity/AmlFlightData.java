package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * AML flight data entity
 *
 * @author ashinisingha
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "aml_flight_data")
public class AmlFlightData extends AbstractDomainBasedEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aml_id", nullable = false, unique = true)
    AircraftMaintenanceLog aircraftMaintenanceLog;

    @Column(name = "aml_id", insertable = false, updatable = false)
    Long amlId;

    private LocalDateTime blockOnTime;
    private LocalDateTime blockOffTime;
    @Column(columnDefinition = "decimal(10,2) default '0.00'")
    private Double blockTime;

    private LocalDateTime landingTime;
    private LocalDateTime takeOffTime;
    @Column(columnDefinition = "decimal(10,2) default '0.00'")
    private Double airTime;

    @Column(columnDefinition = "decimal(10,2) default '0.00'")
    private Double totalAirTime;
    @Column(columnDefinition = "decimal(10,2) default '0.00'")
    private Double grandTotalAirTime;

    private Integer noOfLanding;
    private Integer totalLanding;
    private Integer grandTotalLanding;

    private Double totalApuHours;
    private Integer totalApuCycles;

    private LocalDateTime commencedTime;
    private LocalDateTime completedTime;

    @UpdateTimestamp
    private LocalDate updatedAt;

    private Double apuHours;

    private Integer apuCycles;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AmlFlightData)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((AmlFlightData) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
