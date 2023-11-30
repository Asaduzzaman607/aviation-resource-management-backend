package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import lombok.Value;

@Value(staticConstructor = "of")
public class CityIdNameResponseDto {
    private Long id;
    private String Name;
    private Long countryId;
}
