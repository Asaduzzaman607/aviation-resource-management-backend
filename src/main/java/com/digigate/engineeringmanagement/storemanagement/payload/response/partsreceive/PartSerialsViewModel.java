package com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PartSerialsViewModel {
    private Long partId;
    private String partNo;
    private String partDescription;
    private String vendorSerials;
}
