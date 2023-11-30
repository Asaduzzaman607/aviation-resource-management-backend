package com.digigate.engineeringmanagement.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CaseConverter {
    public static String capitalizeFirstCharacter(String readableString) {
        if(StringUtils.isBlank(readableString)) {
            return StringUtils.EMPTY;
        }
        readableString = StringUtils.normalizeSpace(readableString);
        String[] stringArray = readableString.split(" ");
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < stringArray.length; i++) {
            stringBuffer.append(Character.toUpperCase(stringArray[i].charAt(0))).append(stringArray[i].substring(1)).append(" ");
        }
        return stringBuffer.toString().trim();
    }
    public static String capitalizeAllCharacter(String capitalString) {
        if(StringUtils.isBlank(capitalString)) {
            return StringUtils.EMPTY;
        }
        capitalString = StringUtils.normalizeSpace(capitalString);
        String stringArray = capitalString.toUpperCase();
        return stringArray;
    }
}
