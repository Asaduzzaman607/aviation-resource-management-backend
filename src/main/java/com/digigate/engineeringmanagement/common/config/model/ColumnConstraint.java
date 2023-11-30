package com.digigate.engineeringmanagement.common.config.model;

import com.digigate.engineeringmanagement.common.config.constant.DataType;
import com.digigate.engineeringmanagement.common.config.constant.OperatorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColumnConstraint {
    String value;
    OperatorType operatorType;
    DataType type;
}
