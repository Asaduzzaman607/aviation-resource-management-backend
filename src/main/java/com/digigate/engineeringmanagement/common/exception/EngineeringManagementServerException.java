package com.digigate.engineeringmanagement.common.exception;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import lombok.Data;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

@Data
public class EngineeringManagementServerException extends RuntimeException {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1436995162658277359L;
    /**
     * Error id.
     */
    private final String errorId;

    /**
     * trace id.
     */
    private final String traceId;

    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public EngineeringManagementServerException(String errorId, HttpStatus status, String traceId) {
        this.errorId = errorId;
        this.traceId = traceId;
        this.status = status;
    }

    public static EngineeringManagementServerException badRequest(String errorId) {
        return new EngineeringManagementServerException(errorId, HttpStatus.BAD_REQUEST, MDC.get(
                ApplicationConstant.TRACE_ID));
    }

    public static EngineeringManagementServerException notFound(String errorId) {
        return new EngineeringManagementServerException(errorId, HttpStatus.NOT_FOUND, MDC.get(
                ApplicationConstant.TRACE_ID));
    }

    public static EngineeringManagementServerException dataSaveException(String errorId) {
        return new EngineeringManagementServerException(errorId, HttpStatus.INTERNAL_SERVER_ERROR,
            MDC.get(ApplicationConstant.TRACE_ID));
    }

    public static EngineeringManagementServerException internalServerException(String errorId) {
        return new EngineeringManagementServerException(errorId, HttpStatus.INTERNAL_SERVER_ERROR,
                MDC.get(ApplicationConstant.TRACE_ID));
    }

    public static EngineeringManagementServerException methodNotAllowed(String errorId) {
        return new EngineeringManagementServerException(errorId, HttpStatus.UNAUTHORIZED,
            MDC.get(ApplicationConstant.TRACE_ID));
    }

    public static EngineeringManagementServerException notAuthorized(String errorId) {
        return new EngineeringManagementServerException(
                errorId,
                HttpStatus.FORBIDDEN,
                MDC.get(ApplicationConstant.TRACE_ID)
        );
    }
}
