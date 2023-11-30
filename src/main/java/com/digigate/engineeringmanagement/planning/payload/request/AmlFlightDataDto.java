package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

/**
 * AML flight data dto
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AmlFlightDataDto implements IDto {
    private Long id;
    private Long amlId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime blockOnTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime blockOffTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime landingTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime takeOffTime;
    private Integer noOfLanding;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commencedTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedTime;
    private Double totalAirTime;
    private Integer totalLanding;
    private Double totalApuHours;
    private Integer totalApuCycles;
    private Boolean isActive;
    private Integer pageNo;
    private Character alphabet;
    private Double apuUsedHrs;
    private Integer apuUsedCycle;
}
