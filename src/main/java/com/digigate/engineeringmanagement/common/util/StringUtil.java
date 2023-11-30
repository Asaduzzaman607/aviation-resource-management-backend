package com.digigate.engineeringmanagement.common.util;


import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Random;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.DOT_REGEX;

public class StringUtil {

    public static String buildKey(String modelName, String higherModelName, String positionName, String locationName) {
        return new StringBuilder()
                .append(StringUtils.isBlank(modelName) ? " " : modelName)
                .append(ApplicationConstant.SEPARATOR)
                .append(StringUtils.isBlank(higherModelName) ? " " : higherModelName)
                .append(ApplicationConstant.SEPARATOR)
                .append(StringUtils.isBlank(positionName) ? " " : positionName)
                .append(ApplicationConstant.SEPARATOR)
                .append(StringUtils.isBlank(locationName) ? " " : locationName).toString();
    }

    public static String buildKey(Long modelId, Long partId, Long positionId, Long locationId, String serialNumber) {
        if(StringUtils.isNotBlank(serialNumber)) {
            serialNumber = serialNumber.trim();
        }
        return new StringBuilder()
                .append(Objects.isNull(modelId) ? " " : modelId)
                .append(ApplicationConstant.SEPARATOR)
                .append(Objects.isNull(partId) ? " " : partId)
                .append(ApplicationConstant.SEPARATOR)
                .append(Objects.isNull(positionId) ? " " : positionId)
                .append(ApplicationConstant.SEPARATOR)
                .append(Objects.isNull(locationId) ? " ": locationId)
                .append(ApplicationConstant.SEPARATOR)
                .append(StringUtils.isBlank(serialNumber) ? " " : serialNumber)
                .toString();
    }

    public static String valueOf(Object value) {
        return Objects.isNull(value) ? null : String.valueOf(value);
    }

    public static String buildKey(String originalColumnValue, String reverseColumnValue) {
        return originalColumnValue + ApplicationConstant.SEPARATOR + reverseColumnValue;
    }

    public static String generateRandomString(int length) {
        Random RANDOM = new SecureRandom();
        StringBuilder randomString = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            randomString.append(ApplicationConstant.ALPHABET.charAt(RANDOM.nextInt(ApplicationConstant.ALPHABET.length())));
        }

        return new String(randomString);
    }

    public static String buildKey(String suffix, Long id) {
        if (StringUtils.isNotBlank(suffix)) {
            suffix = suffix.trim();
        }
        return new StringBuilder()
                .append(Objects.nonNull(id) ? id : " ")
                .append(ApplicationConstant.SEPARATOR)
                .append(StringUtils.isNotBlank(suffix) ? suffix : " ")
                .toString();
    }

    public static String buildKey(Long serialId, Long partId) {
        return new StringBuilder()
                .append(Objects.isNull(serialId) ? " " : serialId)
                .append(ApplicationConstant.SEPARATOR)
                .append(Objects.isNull(partId) ? " " : partId)
                .toString();
    }

    public static String parseStringNumber(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }

        String[] strArray = str.split(DOT_REGEX);

        if (strArray.length == 2 && NumberUtil.convertToInteger(strArray[1], -1) == 0) {
            return strArray[0];
        }

        return str;
    }
}
