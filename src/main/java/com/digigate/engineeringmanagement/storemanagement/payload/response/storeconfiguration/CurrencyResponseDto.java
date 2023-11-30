package com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyResponseDto {
    private Long id;
    private String code;
    private String description;
}
