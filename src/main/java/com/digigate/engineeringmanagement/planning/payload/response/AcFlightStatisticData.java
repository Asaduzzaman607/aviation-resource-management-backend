package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AcFlightStatisticData {

    private LocalDate amlDate;
    private Long aircraftId;
    private Double hour;
    private Integer cycle;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AcFlightStatisticData)) return false;
        return Objects.nonNull(this.getAmlDate()) && Objects.nonNull(this.getAircraftId())
                && this.getAmlDate().equals(((AcFlightStatisticData) object).getAmlDate())
                && this.getAircraftId().equals(((AcFlightStatisticData) object).getAircraftId());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getAmlDate())) {
            return this.getClass().hashCode();
        }
        return this.getAmlDate().hashCode();
    }
}
