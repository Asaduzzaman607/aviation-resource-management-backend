package com.digigate.engineeringmanagement.common.payload.request;

import com.digigate.engineeringmanagement.common.config.model.ColumnDefinition;
import com.digigate.engineeringmanagement.common.config.model.ColumnPair;
import com.digigate.engineeringmanagement.common.config.model.OperationMetaData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExcelMetaData {
    private Integer headerSize;
    private List<ColumnDefinition> columnDefinitionList;
    private ColumnPair reversePair;
    private Set<OperationMetaData> operations;
}
