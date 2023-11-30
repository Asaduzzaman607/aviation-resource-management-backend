package com.digigate.engineeringmanagement.common.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmoduleItemTableJson {
    private Long smiId;
    private String table;
    private List<String> uris;
}
