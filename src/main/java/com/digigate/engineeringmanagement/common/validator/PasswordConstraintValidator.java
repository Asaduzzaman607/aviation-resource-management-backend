package com.digigate.engineeringmanagement.common.validator;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    ValidPassword validPassword;

    @Override
    public void initialize(ValidPassword validPassword) {
        this.validPassword = validPassword;
    }


    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(password)) return false;

        if (isPasswordValid(password))
            return true;
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                validPassword.message()).addConstraintViolation();
        return false;
    }

    private boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile(ApplicationConstant.VALID_PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }
}
