package com.digigate.engineeringmanagement.planning.entity;


import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "forecasts")
public class Forecast extends AbstractDomainBasedEntity {
    @Column(nullable = false)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "forecast",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<ForecastAircraft> forecastAircraftSet;

    public void addForecastAircraft(ForecastAircraft forecastAircraft) {
        if (Objects.isNull(forecastAircraftSet)) {
            forecastAircraftSet = new HashSet<>();
        }
        forecastAircraftSet.add(forecastAircraft);
        forecastAircraft.setForecast(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Forecast)) return false;
        return Objects.nonNull(this.getId()) && this.getId().equals(((Forecast) object).getId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getId())) {
            return this.getClass().hashCode();
        }
        return this.getId().hashCode();
    }
}
