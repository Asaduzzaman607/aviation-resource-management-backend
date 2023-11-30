package com.digigate.engineeringmanagement.planning.payload.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * AML flight data View Model
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AmlFlightViewModel {
    private Long id;
    private Long amlId;
    private Integer pageNo;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime blockOnTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime blockOffTime;
    private Double blockTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime landingTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime takeOffTime;
    private Double airTime;
    private Double totalAirTime;
    private Double grandTotalAirTime;
    private Integer noOfLanding;
    private Integer totalLanding;
    private Integer grandTotalLanding;
    private Double totalApuHours;
    private Integer totalApuCycles;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commencedTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedTime;
    private Boolean isActive;
}
