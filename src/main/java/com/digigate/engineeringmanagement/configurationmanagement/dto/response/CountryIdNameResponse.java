package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import lombok.Value;

@Value(staticConstructor = "of")
public class CountryIdNameResponse {
    Long cityId;
    String cityName;
    Long countryId;
    String countryName;
}
