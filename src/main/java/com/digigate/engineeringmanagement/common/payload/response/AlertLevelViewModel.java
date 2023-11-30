package com.digigate.engineeringmanagement.common.payload.response;

import lombok.*;

/**
 * Alert Level ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertLevelViewModel {
    private Integer month;
    private Integer year;
    private Integer pirepOrMarep;
    private Double airTime;
    private Double pirepRate;
    private Double pirepRateMonthRange;
    private Double mean;
    private Double meanBar;
    private Double meanSquare;
}
