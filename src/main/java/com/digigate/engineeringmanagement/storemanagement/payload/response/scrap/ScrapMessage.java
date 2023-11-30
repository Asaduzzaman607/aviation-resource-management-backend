package com.digigate.engineeringmanagement.storemanagement.payload.response.scrap;

import lombok.Value;

import java.util.Set;

@Value(staticConstructor = "of")
public class ScrapMessage {
    String message;
    Set<Long> idList;
}
