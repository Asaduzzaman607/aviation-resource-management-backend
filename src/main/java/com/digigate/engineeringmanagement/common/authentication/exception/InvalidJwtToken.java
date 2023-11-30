package com.digigate.engineeringmanagement.common.authentication.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * JwtTokenNotValid
 * 
 * @author Masud
 *
 */
public class InvalidJwtToken extends AuthenticationServiceException {
    private static final long serialVersionUID = -294671188037098603L;

    /**
     * Constructs an <code>AuthenticationServiceException</code> with the specified
     * message.
     *
     * @param msg the detail message
     */
    public InvalidJwtToken(String msg) {
        super(msg);
    }
}
