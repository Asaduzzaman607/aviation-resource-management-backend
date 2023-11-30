package com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationResponseDto {
    private Long id;
    private String code;
    private String address;
    private Long cityId;
    private String cityName;
    private String countryName;
    private Long countryId;
}
