package com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive;

import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardCommonDto {
    Integer count;
    Integer month;
    Integer year;
    PartStatus partStatus;
    Integer partClassification;
}
