package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import lombok.Value;

@Value(staticConstructor = "of")
public class CityIdNameResponse {
    Long id;
    String name;
}
