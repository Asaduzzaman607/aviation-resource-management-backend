package com.digigate.engineeringmanagement.common.validator;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaxFiveWordValidator implements ConstraintValidator<MaxFiveWordValidation, String> {

    MaxFiveWordValidation maxFiveWordValidation;

    @Override
    public void initialize(MaxFiveWordValidation validPassword) {
        this.maxFiveWordValidation = validPassword;
    }


    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) return true;
        String[] splittedString = value.split(ApplicationConstant.SPACE_REGEX);
        if (splittedString.length < ApplicationConstant.REMARK_MAX_LENGTH) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                maxFiveWordValidation.message()).addConstraintViolation();
        return false;
    }
}
