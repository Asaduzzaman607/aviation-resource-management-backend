package com.digigate.engineeringmanagement.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Documented
@Retention(RUNTIME)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Constraint(validatedBy = MaxFiveWordValidator.class)
public @interface MaxFiveWordValidation {

    int maxWord() default 5;
    String message() default "Maximum valid word for remark field is 5";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
