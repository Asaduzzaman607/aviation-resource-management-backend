package com.digigate.engineeringmanagement.storemanagement.converter;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import org.apache.commons.lang3.StringUtils;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;

public class FileUploadConverter {

    public static String convertNastyStringToData(String date) {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        String[] splitArray = date.split(ApplicationConstant.DATE_TYPE_VALIDATION_REGEX);

        if (date.contains(SLASH) || date.contains(DASH) || date.contains(DOT)) {
            if (splitArray[1].length() < VALUE_TWO) {
                splitArray[1] = VALUE_ZERO + splitArray[1];
            }
            if (splitArray[0].length() < VALUE_TWO) {
                splitArray[0] = VALUE_ZERO + splitArray[0];
            }
            if (splitArray[2].length() < VALUE_FOUR) {
                splitArray[2] = VALUE_TWENTY + splitArray[2];
            }
            return splitArray[0] + SLASH + splitArray[1] + SLASH + splitArray[2];
        }
        return null;
    }

    public static boolean isDate(String givenInput) {
        if (StringUtils.isBlank(givenInput)) {
            return false;
        }
        boolean isDate = false;
        String temporaryValue = givenInput.replaceAll(REPLACE_DATE_TYPE_VALIDATION_REGEX, EMPTY_STRING);

        if (temporaryValue.matches((ALL_INTEGER_VALIDATION_REGEX))) {
            isDate = true;
        }
        return isDate;
    }
}
