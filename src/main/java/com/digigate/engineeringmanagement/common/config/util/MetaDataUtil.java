package com.digigate.engineeringmanagement.common.config.util;

import com.digigate.engineeringmanagement.common.config.constant.DataType;
import com.digigate.engineeringmanagement.common.config.constant.OperatorType;
import com.digigate.engineeringmanagement.common.config.model.ColumnConstraint;
import com.digigate.engineeringmanagement.common.config.model.ColumnDefinition;
import com.digigate.engineeringmanagement.common.config.model.ColumnPair;
import com.digigate.engineeringmanagement.common.config.model.OperationMetaData;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.ExcelMetaData;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class MetaDataUtil {

    private static Set<DataType> supportedDataTypesForEqualAndNotEqualOperation
            = Set.of(DataType.BOOLEAN, DataType.STRING, DataType.PART_CLASSIFICATION, DataType.PART_UNIT_OF_MEASURE ,
            DataType.MODEL_TYPE, DataType.LIFE_CODES, DataType.LIFE_LIMIT_UNIT, DataType.EFFECTIVITY_TYPE,
            DataType.REPETITIVE_TYPE, DataType.TASK_STATUS, DataType.INTERVAL_TYPE);

    public static boolean isValidMetaData(ExcelMetaData excelMetaData) {
        validateExcelMetaData(excelMetaData);
        Integer headerSize = excelMetaData.getHeaderSize();
        List<ColumnDefinition> columnDefinitionList = excelMetaData.getColumnDefinitionList();

        Set<Integer> duplicateIndex = new HashSet<>();
        Set<String> duplicateColumnNames = new HashSet<>();
        validateOperationMetaData(excelMetaData.getOperations(), headerSize, columnDefinitionList);

        for (ColumnDefinition columnDefinition : columnDefinitionList) {
            validateListDataType(columnDefinition);
            validateColumnName(columnDefinition, duplicateColumnNames);
            validateColumnIndex(columnDefinition, headerSize, duplicateIndex);
            validateRegexPattern(columnDefinition);
            validateDefaultValue(columnDefinition);
            isValidDate(columnDefinition);
            validateConstraints(columnDefinition.getConstraints(), columnDefinition.getType());
        }
        return true;
    }

    private static void validateConstraints(Set<ColumnConstraint> constraints, DataType type) {
        if(CollectionUtils.isEmpty(constraints)) {
            return;
        }
        constraints.stream().forEach(constraint -> {
            if(StringUtils.isBlank(constraint.getValue())
                    || Objects.isNull(constraint.getOperatorType()) || Objects.isNull(constraint.getType())) {
                throw EngineeringManagementServerException.internalServerException(ErrorId.INVALID_COLUMNS_CONSTRAINTS);
            }
            if(!type.equals(constraint.getType())) {
                throw EngineeringManagementServerException
                        .internalServerException(ErrorId.DATA_TYPE_MISS_MATCHED_WITH_COLUMN_DATA_TYPE);
            }
            validateConstraintValue(constraint);
        });
    }

    private static void validateConstraintValue(ColumnConstraint constraint) {
        validateOperationType(constraint.getType(), constraint.getOperatorType());
        if (Objects.isNull(DataConverterUtil.getValue(constraint.getValue(), constraint.getType(), null))) {
            throw EngineeringManagementServerException
                    .internalServerException(ErrorId.INVALID_COLUMNS_CONSTRAINT_VALUE);
        }
    }

    private static void isValidDate(ColumnDefinition columnDefinition) {
        if(Objects.isNull(columnDefinition)) {
            return;
        }
        Boolean isValidDate = BooleanUtils.toBoolean(columnDefinition.getIsValidDate());
        if(!isValidDate) {
            return;
        }
        if(!(columnDefinition.getType().equals(DataType.DATE)
                || columnDefinition.getType().equals(DataType.DATE_TIME))) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.INVALID_METADATA_IS_VALID_DATE);
        }
    }

    private static void validateExcelMetaData(ExcelMetaData excelMetaData) {
        if (Objects.isNull(excelMetaData)) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.META_DATA_NOT_FOUND);
        }
        if (Objects.isNull(excelMetaData.getHeaderSize())) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.HEARER_SIZE_IS_REQUIRED);
        }
        Integer headerSize = excelMetaData.getHeaderSize();
        if (CollectionUtils.isEmpty(excelMetaData.getColumnDefinitionList())) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.COLUMN_DEFINITION_LIST_IS_REQUIRED);
        }
        List<ColumnDefinition> columnDefinitionList = excelMetaData.getColumnDefinitionList();
        if (!headerSize.equals(columnDefinitionList.size())) {
            throw EngineeringManagementServerException
                    .internalServerException(ErrorId.HEADER_SIZE_AND_COLUMN_DEFINITION_LIST_SIZE_IS_DIFFERENT);
        }
        validateColumnPair(excelMetaData, headerSize);
    }


    private static void validateListDataType(ColumnDefinition columnDefinition) {
        if (columnDefinition.getType().equals(DataType.LIST)) {
            if (columnDefinition.getDataType().equals(DataType.LIST)) {
                throw EngineeringManagementServerException
                        .internalServerException(ErrorId.LIST_CAN_NOT_BE_OF_TYPE_LIST);
            }
        }
    }

    private static void validateColumnName(ColumnDefinition columnDefinition, Set<String> duplicateColumnNames) {
        if (StringUtils.isBlank(columnDefinition.getName())) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.NAME_CAN_NOT_BE_BLANK);
        }
        String name = columnDefinition.getName().trim();
        columnDefinition.setName(name);
        if (duplicateColumnNames.contains(name)) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.DUPLICATE_COLUMN_NAME);
        }
        duplicateColumnNames.add(name);
    }

    private static void validateOperationMetaData(Set<OperationMetaData> operationMetaDataSet,
                                                  Integer headerSize, List<ColumnDefinition> columnDefinitionList) {
        if (CollectionUtils.isEmpty(operationMetaDataSet)) {
            return;
        }

        operationMetaDataSet.forEach(operationMetaData -> {

            ColumnPair columnPair = operationMetaData.getColumnPair();
            validateColumnOperation(columnPair, headerSize);
            if (Objects.isNull(operationMetaData.getOperatorType())) {
                throw EngineeringManagementServerException
                        .internalServerException(ErrorId.OPERATOR_TYPE_CAN_NOT_BE_NULL);
            }

            DataType firstColumnDataType = columnDefinitionList.get(columnPair.getFirstColumnIndex()).getType();
            DataType secondColumnDataType = columnDefinitionList.get(columnPair.getSecondColumnIndex()).getType();

            if (firstColumnDataType != secondColumnDataType) {
                throw EngineeringManagementServerException
                        .internalServerException(ErrorId.VALUE_CAN_NOT_BE_COMPARED_WITH_DIFFERENT_TYPE);
            }
            validateOperationType(firstColumnDataType, operationMetaData.getOperatorType());
        });
    }

    private static void validateColumnOperation(ColumnPair columnPair, int headerSize) {
        if(Objects.isNull(columnPair)) {
            return;
        }
        if(Objects.isNull(columnPair.getFirstColumnIndex()) || Objects.isNull(columnPair.getSecondColumnIndex())) {
            throw EngineeringManagementServerException
                    .internalServerException(ErrorId.FIRST_AND_SECOND_COLUMN_CAN_NOT_BE_NULL);
        }
        isValidColumnPair(columnPair, headerSize, ErrorId.INVALID_COLUMN_FOR_OPERATIONS);
    }

    private static void validateOperationType(DataType dataType, OperatorType operatorType) {
        if (dataType.equals(DataType.LIST)) {
            throw EngineeringManagementServerException
                    .internalServerException(ErrorId.OPERATION_CAN_NOT_BE_PERFORMED_ON_LIST_DATA);
        }
        if (supportedDataTypesForEqualAndNotEqualOperation.contains(dataType)) {
            if (!(OperatorType.NOT_EQUAL.equals(operatorType) || (OperatorType.EQUAL).equals(operatorType))) {
                throw EngineeringManagementServerException
                        .internalServerException(ErrorId.NOT_EQUAL_COMPARISON_IS_ALLOWED_FOR_BOOLEAN_AND_STRING);
            }
        }
    }

    private static void validateColumnPair(ExcelMetaData excelMetaData, Integer headerSize) {
        isValidColumnPair(excelMetaData.getReversePair(), headerSize, ErrorId.INVALID_COLUMN_NUMBER);
    }

    private static void validateColumnIndex(ColumnDefinition columnDefinition,
                                            Integer headerSize, Set<Integer> duplicateIndex) {
        if (Objects.isNull(columnDefinition.getIndex())) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.COLUMN_NUMBER_IS_REQUIRED);
        }
        int index = columnDefinition.getIndex().intValue();
        if (index >= headerSize || headerSize < 0) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.INVALID_COLUMN_NUMBER);
        }
        if (duplicateIndex.contains(index)) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.DUPLICATE_COLUMN_NUMBER);
        }
        duplicateIndex.add(index);
    }

    private static void validateRegexPattern(ColumnDefinition columnDefinition) {
        String regexPattern = columnDefinition.getRegexPattern();
        if (StringUtils.isNotBlank(regexPattern)) {
            if (!columnDefinition.getType().equals(DataType.STRING)) {
                throw EngineeringManagementServerException
                        .internalServerException(ErrorId.PATTERN_IS_APPLICABLE_ONLY_FOR_STRING);
            }
        }
    }

    private static void validateDefaultValue(ColumnDefinition columnDefinition) {
        if(StringUtils.isBlank(columnDefinition.getDefaultValue())) {
            return;
        }
        columnDefinition.setDefaultValue(columnDefinition.getDefaultValue().trim());
        if (Objects.isNull(DataConverterUtil.getValidDataForOptionalColumn(
                columnDefinition.getDefaultValue(), columnDefinition.getType(), columnDefinition.getDataType()))) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.DUPLICATE_COLUMN_NAME);
        }
    }



    private static void isValidColumnPair(ColumnPair columnPair, int headerSize, String errorId) {
        int columnIndex = NumberUtil.getDefaultIfNull(
                Objects.nonNull(columnPair) ? columnPair.getFirstColumnIndex() : -1, -1);
        int reverseColumnIndex = NumberUtil.getDefaultIfNull(
                Objects.nonNull(columnPair) ? columnPair.getSecondColumnIndex() : -1, -1);

        if (columnIndex >= headerSize
                || reverseColumnIndex >= headerSize
                || (columnIndex == -1 && reverseColumnIndex != -1)
                || (reverseColumnIndex == -1 && columnIndex != -1)) {
            throw EngineeringManagementServerException.internalServerException(errorId);
        }
        if (reverseColumnIndex == columnIndex && columnIndex != -1) {
            throw EngineeringManagementServerException
                    .internalServerException(ErrorId.COLUMN_PAIR_SHOULD_HAVE_DIFFERENT_COLUMN);
        }
    }
}
