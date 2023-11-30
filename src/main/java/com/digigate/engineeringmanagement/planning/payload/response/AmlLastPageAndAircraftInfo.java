package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AmlLastPageAndAircraftInfo dto
 *
 * @author Pranoy Das
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AmlLastPageAndAircraftInfo {
    private Double totalAirTime;
    private Integer totalLanding;
    private Double totalApuHours;
    private Boolean isApuControl = true;
    private Integer pageNo;
    private Character alphabet;
    private Integer MaxPageNo;

    public AmlLastPageAndAircraftInfo(Double totalAirTime, Integer totalLanding, Double totalApuHours) {
        this.totalAirTime = totalAirTime;
        this.totalLanding = totalLanding;
        this.totalApuHours = totalApuHours;
    }

    public AmlLastPageAndAircraftInfo(Double totalAirTime, Integer totalLanding) {
        this.totalAirTime = totalAirTime;
        this.totalLanding = totalLanding;
    }

    public AmlLastPageAndAircraftInfo(Integer pageNo, Character alphabet) {
        this.pageNo = pageNo;
        this.alphabet = alphabet;
    }
}
