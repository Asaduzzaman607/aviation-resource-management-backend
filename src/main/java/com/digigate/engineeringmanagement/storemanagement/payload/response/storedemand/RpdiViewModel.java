package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpdiViewModel {
    private Long returnPartsDetailId;
    private Long PartSerialId;
    private String tsn;
    private String csn;
    private String tso;
    private String cso;
    private String tsr;
    private String csr;
}
