package com.digigate.engineeringmanagement.common.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class UsersResponseMetaDataDto {
    private Map<String, Integer> key;
    private List<List<String>> list;
}

