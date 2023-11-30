package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ManHour Report Dto
 *
 * @author Pranoy Das
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ManHourReportDto {
    private Long ldndId;
    private Integer noOfMan;
    private Double elapsedTime;
    private Double actualManHour;
}
