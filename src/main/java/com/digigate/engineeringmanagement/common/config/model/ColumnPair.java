package com.digigate.engineeringmanagement.common.config.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnPair {
    private Integer firstColumnIndex;
    private Integer secondColumnIndex;
}
