package com.digigate.engineeringmanagement.common.advice;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.ApiError;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementError;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.exception.ErrorCodeReader;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.*;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ApiError apiError = new ApiError();
        EngineeringManagementError engineeringManagementError =
                new EngineeringManagementError(ErrorId.SYSTEM_ERROR, ex.getLocalizedMessage());
        apiError.addError(engineeringManagementError);
        ex.printStackTrace();
        return new ResponseEntity(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object>
    handleConstraintViolationExceptionAllException(ConstraintViolationException ex, WebRequest request) {
        ApiError apiError = new ApiError();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        violations.forEach(violation -> {
            EngineeringManagementError reservationError = getEngineeringManagementError(violation.getMessageTemplate());
            apiError.addError(reservationError);
        });
        return new ResponseEntity(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EngineeringManagementServerException.class)
    public final ResponseEntity<Object> handleEngineeringManagementServerException(
            EngineeringManagementServerException ex, WebRequest request) {
        ApiError apiError = new ApiError();
        EngineeringManagementError reservationError = getEngineeringManagementError(ex.getErrorId());
        apiError.addError(reservationError);
        return new ResponseEntity(apiError, ex.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            EngineeringManagementError reservationError = getEngineeringManagementError(error.getDefaultMessage(),
                    buildErrorMessage(error));
            apiError.addError(reservationError);
        }
        return new ResponseEntity(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError();
        if (e.getMostSpecificCause() instanceof EngineeringManagementServerException) {
            EngineeringManagementServerException reservationServerException = (EngineeringManagementServerException) e.getMostSpecificCause();
            EngineeringManagementError error = getEngineeringManagementError(reservationServerException.getErrorId());
            apiError.addError(error);
            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        } else if (e.getMostSpecificCause() instanceof InvalidFormatException) {
            InvalidFormatException iex = (InvalidFormatException) e.getMostSpecificCause();
            iex.getPath().forEach(reference -> {
                EngineeringManagementError engineeringManagementError = new EngineeringManagementError(ErrorId.INVALID_DATA_FORMAT_EXCEPTION, iex.getOriginalMessage());
                apiError.addError(engineeringManagementError);
            });
            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }
        return handleAllExceptions(e, request);
    }

    private EngineeringManagementError getEngineeringManagementError(String code) {
        EngineeringManagementError engineeringManagementError = ErrorCodeReader.getEngineeringManagementError(code);
        if (Objects.isNull(engineeringManagementError)) {
            return ErrorCodeReader.getErrorByMessage(code);
        }
        return engineeringManagementError;
    }

    private EngineeringManagementError getEngineeringManagementError(String code, String message) {
        EngineeringManagementError engineeringManagementError = ErrorCodeReader.getEngineeringManagementError(code);
        if (Objects.isNull(engineeringManagementError)) {
            return ErrorCodeReader.getErrorByMessage(message);
        }
        return engineeringManagementError;
    }

    private String buildErrorMessage(FieldError error) {
        return capitalize(StringUtils.join(splitByCharacterTypeCamelCase(emptyFieldErrorIfNull(error)
        ), SPACE)) + SPACE + error.getDefaultMessage();
    }

    private String emptyFieldErrorIfNull(FieldError fieldError) {
        return Objects.isNull(fieldError) ? ApplicationConstant.EMPTY_STRING : fieldError.getField();
    }
}
