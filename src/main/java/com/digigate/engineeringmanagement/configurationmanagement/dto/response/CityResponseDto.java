package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityResponseDto {
    private Long id;
    private String name;
    private Long countryId;
    private String countryName;
    private String dialingCode;
    private String zipCode;
}
