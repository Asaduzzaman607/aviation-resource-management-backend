package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Daily Flying Hours Total Model
 *
 * @author Sayem Hasnat
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DailyHrsReportTotalModel {
    private Integer noOfLanding =0;
    private Double totalAirTime=0.0;
    private Double grandTotalAirTime=0.0;
    private Integer grandTotalLanding = 0;
    private Double engineOil1=0.0;
    private Double engineOil2=0.0;
    private Double apuOil=0.0;
}
