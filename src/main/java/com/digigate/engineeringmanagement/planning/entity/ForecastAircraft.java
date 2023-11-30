package com.digigate.engineeringmanagement.planning.entity;

import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "forecasts_aircrafts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"aircraft_id", "forecast_id"})
})
public class ForecastAircraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    @Column(name = "aircraft_id", insertable = false, updatable = false, nullable = false)
    private Long aircraftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forecast_id", nullable = false)
    private Forecast forecast;

    @Column(name = "forecast_id", nullable = false, insertable = false, updatable = false)
    private Long forecastId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "forecastAircraft",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<ForecastTask> forecastTaskSet;

    public void addForecastTask(ForecastTask forecastTask) {
        if (Objects.isNull(forecastTaskSet)) {
            forecastTaskSet = new HashSet<>();
        }
        forecastTaskSet.add(forecastTask);
        forecastTask.setForecastAircraft(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ForecastAircraft)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((ForecastAircraft) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
