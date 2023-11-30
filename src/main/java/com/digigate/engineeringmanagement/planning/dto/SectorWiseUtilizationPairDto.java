package com.digigate.engineeringmanagement.planning.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Objects;

/**
 * This is a Sector-wise Utilization pair dto for main report data
 *
 * @author Sayem Hasnat
 */
@Data
@Builder
public class SectorWiseUtilizationPairDto {
    private String sector;
    private String flightNo;
    private Integer totalHours;
    private Integer totalCycle;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectorWiseUtilizationPairDto that = (SectorWiseUtilizationPairDto) o;
        return Objects.equals(getSector(), that.getSector()) && Objects.equals(getFlightNo(), that.getFlightNo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSector(), getFlightNo());
    }
}

