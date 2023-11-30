package com.digigate.engineeringmanagement.storeinspector.payload.response.storeinspector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartLifeStatusResponseDto {
    private Double tsn;
    private Integer csn;
    private Double tsr;
    private Integer csr;
    private Double tso;
    private Integer cso;
    private Boolean isOverHaul = Boolean.FALSE;
    private Boolean isShopCheck = Boolean.FALSE;
    private Boolean isPresent;
}
