package com.digigate.engineeringmanagement.configurationmanagement.dto.request.search;

import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalSettingSearchDto implements SDto {
    private String workFlowActionName;
    private String subModuleItemName;
    private Boolean isActive = true;
}
