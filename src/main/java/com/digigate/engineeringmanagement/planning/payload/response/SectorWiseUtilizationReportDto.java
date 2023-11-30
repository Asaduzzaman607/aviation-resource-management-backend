package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.util.Objects;

/**
 * Sector-wise Utilization Report Data Dto
 *
 * @author Sayem Hasnat
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SectorWiseUtilizationReportDto {
    private String fromAirportIataCode;
    private String toAirportIataCode;
    private String sector;
    private String flightNo;
    private Double totalHours;
    private Integer totalCycle;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectorWiseUtilizationReportDto that = (SectorWiseUtilizationReportDto) o;
        return sector.equals(that.sector);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sector);
    }

    public SectorWiseUtilizationReportDto(String fromAirportIataCode, String toAirportIataCode,
                                          String flightNo, Double totalHours, Integer totalCycle) {
        this.fromAirportIataCode = fromAirportIataCode;
        this.toAirportIataCode = toAirportIataCode;
        this.flightNo = flightNo;
        this.totalHours = totalHours;
        this.totalCycle = totalCycle;
    }

}

