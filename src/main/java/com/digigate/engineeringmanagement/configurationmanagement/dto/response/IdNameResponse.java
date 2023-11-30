package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import lombok.Value;

@Value(staticConstructor = "of")
public class IdNameResponse {
    Long id;
    String name;
}
