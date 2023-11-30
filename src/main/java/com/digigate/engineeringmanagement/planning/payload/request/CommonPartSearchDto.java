package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import lombok.Data;

@Data
public class CommonPartSearchDto {
    private PartClassification partClassification = PartClassification.ROTABLE;
    private Boolean isActive = true;
    private String partNo;
}
