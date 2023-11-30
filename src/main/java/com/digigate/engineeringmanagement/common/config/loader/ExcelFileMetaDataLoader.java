package com.digigate.engineeringmanagement.common.config.loader;

import com.digigate.engineeringmanagement.common.config.util.MetaDataUtil;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.request.ExcelMetaData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExcelFileMetaDataLoader {

    private final ObjectMapper mapper;
    private static Map<String, ExcelMetaData> excelMetaDataMap;
    private final Environment environment;
    private static final String KEY_ARM_EXCEL_FILE_NAMES = "arm.excel.file.names";
    private static final String COMMA = ",";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelFileMetaDataLoader.class);

    @Autowired
    public ExcelFileMetaDataLoader(ObjectMapper mapper, Environment environment) {
        this.mapper = mapper;
        this.environment = environment;
        excelMetaDataMap = new ConcurrentHashMap<>();
        loadConfigJson();
    }

    private void loadConfigJson() {

        String fileNamesAsString = environment.getProperty(KEY_ARM_EXCEL_FILE_NAMES);
        if (StringUtils.isBlank(fileNamesAsString)) {
            return;
        }

        List<String> fileNames = Arrays.asList(fileNamesAsString.split(COMMA));
        if (CollectionUtils.isEmpty(fileNames)) {
            return;
        }

        fileNames.forEach(file -> {
            InputStream inputStream = getClass()
                    .getClassLoader().getResourceAsStream(
                            ApplicationConstant.META_DATA_BASE_PATH + file + ApplicationConstant.JSON_EXTENSION);
            try {
                String data = readFromInputStream(inputStream);
                ExcelMetaData excelMetaData = mapper.readValue(data, ExcelMetaData.class);
                MetaDataUtil.isValidMetaData(excelMetaData);
                excelMetaDataMap.put(file, excelMetaData);
            } catch (Exception e ) {
                LOGGER.error("Unable to parse metadata config json error: {}", e);
            }
        });
    }

    private String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public static ExcelMetaData getMetaData(String fileName) {
        ExcelMetaData excelMetaData = excelMetaDataMap.get(fileName);
        if(Objects.isNull(excelMetaData)) {
            throw EngineeringManagementServerException.internalServerException(ErrorId.META_DATA_NOT_FOUND);
        }
        return excelMetaData;
    }
}
