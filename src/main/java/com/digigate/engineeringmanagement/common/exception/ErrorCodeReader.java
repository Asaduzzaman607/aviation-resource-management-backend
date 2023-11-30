package com.digigate.engineeringmanagement.common.exception;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.FIRST_INDEX;

@Component
public class ErrorCodeReader {
    public static Map<String, EngineeringManagementError> errorMap = new HashMap<>();

    private final ObjectMapper mapper;

    @Autowired
    public ErrorCodeReader(ObjectMapper mapper) {
        this.mapper = mapper;
        readErrorCode();
    }

    private void readErrorCode() {
        InputStream inputStream = getClass()
                .getClassLoader().getResourceAsStream(ApplicationConstant.ERROR_CODE_JSON_FILE);
        try {
            String data = readFromInputStream(inputStream);
            ApiError apiError = mapper.readValue(data, ApiError.class);
            errorMap = apiError.getApiErrors().stream()
                    .collect(Collectors.toMap(EngineeringManagementError::getCode,
                            Function.identity()));
        } catch (IOException e) {
            System.out.println("Unable to parse error code json: " + e.getMessage());
        }
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

    public static EngineeringManagementError getEngineeringManagementError(String code) {
        return errorMap.get(code);
    }

    public static EngineeringManagementError getErrorByMessage(String message) {
        EngineeringManagementError error = new EngineeringManagementError();
        if (message.contains(ApplicationConstant.MESSAGE_SEPARATOR)) {
            return getDynamicError(message);
        }
        error.setCode(ErrorId.COMMON_FIELD_ERROR);
        error.setMessage(message);
        return error;
    }

    private static EngineeringManagementError getDynamicError(String message) {
        List<String> stringList = new LinkedList<>(Arrays.asList(message.split(ApplicationConstant.MESSAGE_SEPARATOR)));
        String mainString = stringList.get(FIRST_INDEX);
        EngineeringManagementError engineeringManagementError = errorMap.getOrDefault(mainString,
                getEngineeringManagementError(ErrorId.SYSTEM_ERROR));
        stringList.remove(FIRST_INDEX);
        EngineeringManagementError dynamicError = new EngineeringManagementError();
        dynamicError.setCode(engineeringManagementError.getCode());
        dynamicError.setMessage(String.format(engineeringManagementError.getMessage(), stringList));
        return dynamicError;
    }
}
