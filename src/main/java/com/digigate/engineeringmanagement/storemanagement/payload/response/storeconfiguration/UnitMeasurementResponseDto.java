package com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UnitMeasurementResponseDto {
    private Long id;
    private String code;
}
