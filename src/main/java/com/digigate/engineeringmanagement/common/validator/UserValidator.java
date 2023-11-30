package com.digigate.engineeringmanagement.common.validator;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserValidator {

    public static boolean isValidConfirmPassword(String password, String confirmPassword) {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword)) {
            return false;
        }
        return StringUtils.equals(password, confirmPassword);
    }

    public static boolean isValidPassword(String password) {
        if(StringUtils.isBlank(password)) {
            return false;
        }
        final String regex = ApplicationConstant.VALID_PASSWORD_REGEX;
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
