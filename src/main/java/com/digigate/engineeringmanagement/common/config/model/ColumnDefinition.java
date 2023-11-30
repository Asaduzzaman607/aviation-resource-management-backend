package com.digigate.engineeringmanagement.common.config.model;


import com.digigate.engineeringmanagement.common.config.constant.DataType;
import lombok.*;

import java.util.Set;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColumnDefinition {
    private String name;
    private Integer index;
    private Boolean isRequired = false;
    private Boolean isUnique = false;
    private DataType type = DataType.STRING;
    private DataType dataType = DataType.STRING;
    private String defaultValue;
    private String regexPattern;
    private Boolean isValidDate;
    private Set<ColumnConstraint> constraints;
}
