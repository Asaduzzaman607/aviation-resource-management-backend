package com.digigate.engineeringmanagement.common.config.util;

import com.digigate.engineeringmanagement.common.config.constant.DataType;
import com.digigate.engineeringmanagement.common.config.constant.OperatorType;
import com.digigate.engineeringmanagement.common.config.loader.ExcelFileMetaDataLoader;
import com.digigate.engineeringmanagement.common.config.model.*;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.ExcelMetaData;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.common.util.StringUtil;
import com.digigate.engineeringmanagement.planning.constant.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Excel File Util
 *
 * @author Masud Rana
 */
public class ExcelFileUtil {

    public static ExcelData getExcelDataFromSheet(MultipartFile file, String metaDataJsonFileName, String sheetName) {
        if (Objects.isNull(file) || file.getSize() == 0) {
            throw EngineeringManagementServerException.badRequest(ErrorId.EXCEL_FILE_IS_REQUIRED);
        }
        if (!hasExcelFormat(file)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_FILE_FORMAT);
        }
        ExcelMetaData excelMetaData = ExcelFileMetaDataLoader.getMetaData(metaDataJsonFileName);

        ExcelData excelData = getDataFromSheet(file, sheetName, excelMetaData);

        if (CollectionUtils.isNotEmpty(excelData.getErrorMessages())) {
            return excelData;
        }
        if (CollectionUtils.isEmpty(excelData.getDataList())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.FILE_ROWS_NOT_FOUND);
        }
        return excelData;
    }

    private static boolean hasExcelFormat(MultipartFile file) {
        file.getContentType();
        return file.getContentType().equals(ApplicationConstant.EXCEL_CONTENT_TYPE);
    }


    public static ExcelData getDataFromSheet(MultipartFile file, String fileName, ExcelMetaData excelMetaData) {

        List<ColumnDefinition> columnDefinitionList = excelMetaData.getColumnDefinitionList();
        int headerSize = excelMetaData.getHeaderSize();
        ColumnPair columnPair = excelMetaData.getReversePair();

        int firstColumnIndex = NumberUtil.getDefaultIfNull(
                Objects.nonNull(columnPair)
                        ? columnPair.getFirstColumnIndex() : -1, -1);
        int secondColumnIndex =
                NumberUtil.getDefaultIfNull(Objects.nonNull(columnPair)
                        ? columnPair.getSecondColumnIndex() : -1, -1);

        Sheet sheet = getSheet(file, fileName);

        if(Objects.isNull(sheet)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.SHEET_IS_EMPTY_OR_NOT_FOUND);
        }

        List<Map<String, ?>> dataList = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        boolean isTitleRow = true;

        Map<String, Integer> duplicateMap = new HashedMap();

        List<Integer> uniqueColumns = columnDefinitionList.stream()
                .filter(columnDefinition -> BooleanUtils.toBoolean(columnDefinition.getIsUnique()))
                .map(columnDefinition -> columnDefinition.getIndex()).collect(Collectors.toList());

        String uniqueColumnAsString = "";
        List<String> errorList = new ArrayList<>();
        Set<String> reverseEntityKeys = new HashSet<>();
        Set<OperationMetaData> operations = excelMetaData.getOperations();

        for (Row row : sheet) {
            if (isEmptyRow(row, headerSize)) {
                continue;
            }
            if (isTitleRow) {
                isTitleRow = false;
                headers = getHeaders(row, headerSize, columnDefinitionList);
                uniqueColumnAsString = getUniqueColumns(uniqueColumns, headers);
                continue;
            }

            StringBuilder stringBuilder = new StringBuilder();
            List<String> errorMessages = new ArrayList<>();
            int rowNumber = row.getRowNum() + 1;

            Map<String, Object> map = iterateThroughColumn(
                    row, headerSize, stringBuilder, columnDefinitionList, errorMessages, rowNumber);

            validateOperationData(operations, rowNumber, columnDefinitionList, map, errorMessages);

            if (firstColumnIndex != -1 && secondColumnIndex != -1) {
                checkForReverseEntity(row, secondColumnIndex,
                        firstColumnIndex, reverseEntityKeys, errorMessages, rowNumber, headers);
            }

            String key = stringBuilder.toString();
            checkAndAddErrorForDuplicateRow(row, CollectionUtils.isNotEmpty(uniqueColumns)
                    && duplicateMap.containsKey(key), errorMessages, errorList, uniqueColumnAsString);
            duplicateMap.put(key, rowNumber);
            dataList.add(map);
        }

        ExcelData excelData = ExcelData.builder()
                .dataList(dataList)
                .build();
        if (CollectionUtils.isNotEmpty(errorList)) {
            excelData.setErrorMessages(errorList);
        }
        return excelData;
    }

    private static String getUniqueColumns(List<Integer> uniqueColumns, List<String> headers) {
        if(CollectionUtils.isEmpty(uniqueColumns)) {
            return StringUtils.EMPTY;
        }
        StringBuilder stringBuilder = new StringBuilder();
        uniqueColumns.stream().forEach(column-> stringBuilder
                .append(ApplicationConstant.SEPARATOR).append(headers.get(column)));
        return stringBuilder.toString();
    }

    public static Sheet getSheet(MultipartFile file, String sheetName) {
        if (Objects.isNull(file)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.FILE_CAN_NOT_BE_NULL);
        }
        if (StringUtils.isBlank(sheetName)) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.FILE_NAME_CAN_NOT_BE_NULL);
        }

        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheet(sheetName);
            workbook.close();
            return sheet;
        } catch (IOException e) {
            throw EngineeringManagementServerException.badRequest(ErrorId.FILE_ERROR);
        }
    }

    public static List<String> getHeaders(Row row, int headerSize, List<ColumnDefinition> columnDefinitionList) {
        if (Objects.isNull(row)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.FILE_HEADER_CAN_NOT_BE_NULL);
        }
        if (headerSize < 1) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.INVALID_HEADER_SIZE);
        }

        List<String> headers = new ArrayList<>();

        for (int ind = 0; ind < headerSize; ind++) {
            if (Objects.isNull(row.getCell(ind))) {
                throw EngineeringManagementServerException.badRequest(ErrorId.HEADER_CELL_IS_MISSING);
            }
            String name = getCellStringValue(row.getCell(ind));
            if (StringUtils.isBlank(name)) {
                throw EngineeringManagementServerException.badRequest(ErrorId.HEADER_CELL_IS_MISSING);
            }
            if (!name.equals(columnDefinitionList.get(ind).getName())) {
                throw EngineeringManagementServerException.badRequest(ErrorId.COLUMN_NOT_RECOGNIZED);
            }
            headers.add(name);
        }

        return headers;
    }

    private static Map<String, Object> iterateThroughColumn(Row row,
                                                            int headerSize, StringBuilder stringBuilder,
                                                            List<ColumnDefinition> columnDefinitionList,
                                                            List<String> errorMessages, int rowNumber) {

        Map<String, Object> map = new HashMap<>();

        for (int ind = 0; ind < headerSize; ind++) {
            ColumnDefinition columnDefinition = columnDefinitionList.get(ind);
            String header = columnDefinition.getName();
            String stringValue = getStringValue(columnDefinition, row, errorMessages, rowNumber);
            if (StringUtils.isNotBlank(stringValue)) {
                stringValue = stringValue.trim();
                Object value = getValue(columnDefinition.getIsRequired(), stringValue, errorMessages,
                        rowNumber, header, columnDefinition.getType(), columnDefinition.getDataType());
                map.put(header, value);
                if (columnDefinition.getIsUnique()) {
                    stringBuilder.append(ApplicationConstant.SEPARATOR).append(stringValue);
                }
                validateStringFormat(columnDefinition, stringValue, errorMessages, header, rowNumber);
                validateConstraintsData(value, columnDefinition, errorMessages, rowNumber);
                if (!validateDate(value, columnDefinition.getIsValidDate(), columnDefinition.getType())) {
                    errorMessages.add(String.format("value: {%s} of column: {%s} should be greater" +
                                    " than or equal current date time at row: {%s}",
                            stringValue, header, rowNumber));
                }
            }
        }
        map.put(ApplicationConstant.ROW_NUMBER, String.valueOf(rowNumber));
        return map;
    }

    private static boolean validateDate(Object value, Boolean isValidDate, DataType dataType) {
        if(Objects.isNull(value) || Objects.isNull(isValidDate) || Objects.isNull(dataType)) {
            return true;
        }
        if(!(dataType.equals(DataType.DATE) || dataType.equals(DataType.DATE_TIME))) {
            return true;
        }
        if(dataType.equals(DataType.DATE)) {
            return DateUtil.compareValue((LocalDate)value, LocalDate.now(), OperatorType.GREATER_THAN_OR_EQUAL);
        } else if(dataType.equals(DataType.DATE_TIME)) {
            return DateUtil.compareValue((LocalDateTime) value, LocalDateTime.now(), OperatorType.GREATER_THAN_OR_EQUAL);
        }
        return true;
    }

    private static void validateOperationData(Set<OperationMetaData> operationMetaDataSet, Integer rowNumber,
                                              List<ColumnDefinition> columnDefinitionList,
                                              Map<String, Object> data, List<String> errorMessages) {
        if (CollectionUtils.isEmpty(operationMetaDataSet) || MapUtils.isEmpty(data)) {
            return;
        }

        operationMetaDataSet.stream().forEach(operationMetaData -> {
            OperatorType operatorType = operationMetaData.getOperatorType();
            ColumnPair columnPair = operationMetaData.getColumnPair();
            Integer firstColumnIndex = columnPair.getFirstColumnIndex();
            Integer secondColumnIndex = columnPair.getSecondColumnIndex();
            ColumnDefinition firstColumnDefinition = columnDefinitionList.get(firstColumnIndex);
            ColumnDefinition secondColumnDefinition = columnDefinitionList.get(secondColumnIndex);
            Object firstValue = data.get(firstColumnDefinition.getName());
            Object secondValue = data.get(secondColumnDefinition.getName());
            DataType dataType = firstColumnDefinition.getType();

            if (!compareValue(firstValue, secondValue, dataType, operatorType)) {
                errorMessages.add(String.format("Value of column: {%s} should be {%s} of column : {%s} at row : {%s}",
                        firstColumnDefinition.getName(),
                        operatorType.getType(), secondColumnDefinition.getName(), rowNumber));
            }
        });

    }

    private static void validateConstraintsData(Object value, ColumnDefinition columnDefinition,
                                                List<String> errorMessages, Integer rowNumber) {
        if (CollectionUtils.isEmpty(columnDefinition.getConstraints())) {
            return;
        }
        columnDefinition.getConstraints().stream().forEach(constraint -> {
            Object referenceValue = DataConverterUtil.getValue(
                    constraint.getValue(), constraint.getType(), null);
            if (!compareValue(value, referenceValue, constraint.getType(), constraint.getOperatorType())) {
                errorMessages.add(String.format("Value of column: {%s} should be {%s}  with value : {%s} at row : {%s}",
                        columnDefinition.getName(), constraint.getOperatorType(), referenceValue, rowNumber));
            }
        });
    }

    private static boolean compareValue(Object firstValue, Object secondValue, DataType dataType,
                                        OperatorType operatorType) {
        if (Objects.isNull(firstValue) || Objects.isNull(secondValue)) {
            return true;
        }
        if (dataType.equals(DataType.DOUBLE)) {
            return NumberUtil.compareValue((double) firstValue, (double) secondValue, operatorType);
        } else if (dataType.equals(DataType.LONG)) {
            return NumberUtil.compareValue((long) firstValue, (long) secondValue, operatorType);
        } else if (dataType.equals(DataType.INT)) {
            return NumberUtil.compareValue((int) firstValue, (int) secondValue, operatorType);
        } else if (dataType.equals(DataType.DATE)) {
            return DateUtil.compareValue((LocalDate) firstValue, (LocalDate) secondValue, operatorType);
        } else if (dataType.equals(DataType.DATE_TIME)) {
            return DateUtil.compareValue((LocalDateTime) firstValue, (LocalDateTime) secondValue, operatorType);
        } else if (dataType.equals(DataType.BOOLEAN)) {
            return NumberUtil.compareValue((boolean) firstValue, (boolean) secondValue, operatorType);
        } else if (dataType.equals((DataType.STRING))) {
            return NumberUtil.compareValue((String) firstValue, (String) secondValue, operatorType);
        } else if (dataType.equals(DataType.PART_CLASSIFICATION)) {
            return NumberUtil.compareValue(
                    (PartClassification) firstValue, (PartClassification) secondValue, operatorType);
        } else if(dataType.equals(DataType.MODEL_TYPE)){
            return NumberUtil.compareValue(
                    (ModelType) firstValue, (ModelType) secondValue, operatorType
            );
        } else if(dataType.equals(DataType.LIFE_CODES)){
            return NumberUtil.compareValue(
                    (LifeCodes) firstValue, (LifeCodes) secondValue, operatorType
            );
        } else if(dataType.equals(DataType.LIFE_LIMIT_UNIT)){
            return NumberUtil.compareValue(
                    (LifeLimitUnit) firstValue, (LifeLimitUnit) secondValue, operatorType
            );
        } else if(dataType.equals(DataType.EFFECTIVITY_TYPE)){
            return NumberUtil.compareValue(
                    (EffectivityType) firstValue, (EffectivityType) secondValue, operatorType
            );
        } else if(dataType.equals(DataType.INTERVAL_TYPE)){
            return NumberUtil.compareValue(
                    (IntervalType) firstValue, (IntervalType) secondValue, operatorType
            );
        } else if(dataType.equals(DataType.REPETITIVE_TYPE)){
            return NumberUtil.compareValue(
                    (RepetitiveTypeEnum) firstValue, (RepetitiveTypeEnum) secondValue, operatorType
            );
        } else if(dataType.equals(DataType.TASK_STATUS)){
            return NumberUtil.compareValue(
                    (TaskStatusEnum) firstValue, (TaskStatusEnum) secondValue, operatorType
            );
        }

        return true;
    }

    private static void checkForReverseEntity(Row row, int reverseColumnIndex, int columnIndex,
                                              Set<String> reverseEntityKeys,
                                              List<String> errorMessages, int rowNumber, List<String> headers) {
        String reverseColumnValue = getCellStringValue(row.getCell(reverseColumnIndex));
        String originalColumnValue = getCellStringValue(row.getCell(columnIndex));

        String reverseEntity = StringUtil.buildKey(reverseColumnValue, originalColumnValue);
        String originalEntity = StringUtil.buildKey(originalColumnValue, reverseColumnValue);
        if (reverseEntityKeys.contains(originalEntity)) {
            errorMessages.add(String.format("Reverse entity for {%s} and {%s} " +
                            "exists with value {%s} : {%s}  at row : {%s} at file", headers.get(columnIndex),
                    headers.get(reverseColumnIndex), originalColumnValue, reverseColumnValue, rowNumber));
        }
        reverseEntityKeys.add(reverseEntity);
    }

    private static String getCellStringValue(Cell cell) {
        if (Objects.isNull(cell)) {
            return " ";
        }
        DataFormatter df = new DataFormatter();
        String value = df.formatCellValue(cell).trim();
        return StringUtils.isNotBlank(value) ? value : " ";
    }

    private static void checkAndAddErrorForDuplicateRow(Row row, boolean needDuplicateChecking,
                                                        List<String> errorMessages,
                                                        List<String> errorList, String uniqueColumnAsString) {
        if (needDuplicateChecking) {
            errorList.add(String.format("Duplicate row exists with same configuration for columns: {%s} at row : {%s}",
                    uniqueColumnAsString, row.getRowNum() + 1));
        }
        if (CollectionUtils.isNotEmpty(errorMessages)) {
            errorList.addAll(errorMessages);
        }
    }

    private static boolean isEmptyRow(Row row, int columnSize) {
        for (int i = 0; i < columnSize; i++) {
            String value = getCellStringValue(row.getCell(i));
            if (StringUtils.isNotBlank(value)) {
                return false;
            }
        }
        return true;
    }

    private static void validateStringFormat(ColumnDefinition columnDefinition, String stringValue,
                                             List<String> errorMessages, String header, Integer rowNumber) {
        if (!matchesRegexPattern(columnDefinition.getRegexPattern(), stringValue)) {
            errorMessages.add(String.format("Pattern is not matching for" +
                    " value : {%s} at column: {%s} at row: {%s}", stringValue, header, rowNumber));
        }
    }

    private static boolean matchesRegexPattern(String regexPattern, String value) {
        if (StringUtils.isBlank(regexPattern) || StringUtils.isBlank(value)) {
            return true;
        }
        return value.matches(regexPattern);
    }

    private static String getStringValue(ColumnDefinition columnDefinition,
                                         Row row, List<String> errorMessages, int rowNumber) {
        String defaultValue = columnDefinition.getDefaultValue();
        String stringValue = getCellStringValue(row.getCell(columnDefinition.getIndex().intValue()));

        if(columnDefinition.getType().getType().equals("int") || columnDefinition.getType().getType().equals("long") ){
            stringValue = stringValue.split("\\.")[0];
        }

        if (BooleanUtils.toBoolean(columnDefinition.getIsRequired())
                && StringUtils.isBlank(stringValue)) {
            errorMessages.add(String.format("{%s} is null at row : {%s}", columnDefinition.getName(), rowNumber));
        }

        if (StringUtils.isBlank(stringValue)) {
            return StringUtils.isBlank(defaultValue) ? " " : defaultValue;
        }
        return stringValue;
    }

    private static Object getValue(Boolean isRequired, String stringValue, List<String> errorMessages,
                                   Integer rowNumber, String header, DataType type, DataType dataType) {
        if (BooleanUtils.toBoolean(isRequired)) {
            return DataConverterUtil
                    .getValueForRequiredField(stringValue, errorMessages, rowNumber, header, type, dataType);
        } else {
            return DataConverterUtil
                    .getValueForOptionalField(stringValue, errorMessages, rowNumber, header, type, dataType);
        }
    }

    public static boolean addErrorIfKeyNotExists(String keyName,
                                                 String column,
                                                 Map dataMap, Integer rowNumber, List<String> errorMessages) {
        if (!dataMap.containsKey(keyName)) {
            errorMessages.add(String.format("{%s}: {%s} is not present or" +
                    " not active at row : {%s}", column, keyName, rowNumber));
            return false;
        }
        return true;
    }

    public static boolean addErrorIfKeyNotExists(String keyName,
                                                 String key,
                                                 String column,
                                                 String relatedKey,
                                                 String relatedKeyColumn,
                                                 Map dataMap, Integer rowNumber, List<String> errorMessages) {
        if (!dataMap.containsKey(keyName)) {
            errorMessages.add(String.format("{%s}: {%s} is not applicable for this {%s}: {%s}" +
                    " at row : {%s}", column, key, relatedKeyColumn, relatedKey, rowNumber));
            return false;
        }
        return true;
    }
    public static ExcelDataResponse prepareErrorResponse(List<String> errorMessage) {
        return ExcelDataResponse.builder()
                .errorMessages(errorMessage)
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    public static ExcelDataResponse prepareSuccessResponse() {
        return ExcelDataResponse.builder()
                .successMessage(ApplicationConstant.CREATED_SUCCESSFULLY_MESSAGE)
                .status(HttpStatus.OK)
                .build();
    }
}
