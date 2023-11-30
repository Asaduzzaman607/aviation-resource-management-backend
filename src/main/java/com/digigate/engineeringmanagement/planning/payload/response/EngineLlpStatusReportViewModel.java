package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class EngineLlpStatusReportViewModel {
    private LastShopVisitedInfoViewModel lastShopVisitedInfoViewModel;
    private CurrentTimesViewModel currentTimesViewModel;
    private ShopVisitedInformation shopVisitedInformation;
    private EngineInstallationInfoViewModel engineInstallationInfoViewModel;
    List<EngineLlpPartViewModel> engineLlpPartViewModels;
}
