package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import lombok.Value;

@Value(staticConstructor = "of")
public class ItemPartViewModel {
    private Long partId;
    private String partNo;
    private String partDescription;
}
