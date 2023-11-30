package com.digigate.engineeringmanagement.common.payload.response;

import lombok.*;

import java.util.List;

/**
 * Alert Level Model
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertLevelReport {
    List<AlertLevelViewModel> alertLevelViewModelList;
    Double alertLevel;
    Double meanXBar;
    Double totalPirepRateWithThereeMonths;
    Double totalMeanBarSquare;
    Double Sd;
}
