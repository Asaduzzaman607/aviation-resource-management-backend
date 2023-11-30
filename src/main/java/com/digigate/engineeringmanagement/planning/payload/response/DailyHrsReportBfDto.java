package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

/**
 * Daily Hrs Report PreviousDay Data Dto
 *
 * @author Sayem Hasnat
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyHrsReportBfDto {
    private Double grandTotalAirTime; //TAT
    private Integer grandTotalLanding; //TAC
}
