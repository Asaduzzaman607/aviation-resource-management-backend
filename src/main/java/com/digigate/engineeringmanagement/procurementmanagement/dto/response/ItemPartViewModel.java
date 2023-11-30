package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import lombok.Value;

@Value(staticConstructor = "of")
public class ItemPartViewModel {
    private Long id;
    private Long partId;
    private String partNo;
    private String partDescription;
}
