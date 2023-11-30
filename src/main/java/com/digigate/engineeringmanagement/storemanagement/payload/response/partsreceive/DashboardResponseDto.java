package com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DashboardResponseDto {
    List<DashboardCommonDto> storeDemandData;
    List<DashboardCommonDto> storeIssueData;
    List<DashboardCommonDto> procurementRequisitionData;
    List<DashboardCommonDto> returnPartInfo;
    List<DashboardCommonDto> scrapPartInfo;

}
