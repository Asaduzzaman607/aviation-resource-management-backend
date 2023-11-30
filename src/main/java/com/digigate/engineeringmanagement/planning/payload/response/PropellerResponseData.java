package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PropellerResponseData {
    private PropellerReportHeaderData propellerReportHeaderData;
    private List<PropellerReportViewModel> propellerReportViewModelList;
}
