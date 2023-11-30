package com.digigate.engineeringmanagement.common.authentication.controllers;


import com.digigate.engineeringmanagement.common.authentication.payload.request.LoginRequest;
import com.digigate.engineeringmanagement.common.authentication.payload.request.TokenRefreshRequest;
import com.digigate.engineeringmanagement.common.authentication.payload.response.JwtResponse;
import com.digigate.engineeringmanagement.common.authentication.payload.response.TokenRefreshResponse;
import com.digigate.engineeringmanagement.common.authentication.service.AuthService;
import com.digigate.engineeringmanagement.common.authentication.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    /**
     * Parameterized constructor
     *
     * @param authService         {@link AuthService}
     * @param refreshTokenService {@link RefreshTokenService}
     */
    @Autowired
    public AuthController(AuthService authService, RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * An API endpoint to login
     *
     * @param loginRequest {@link LoginRequest}
     * @return {@link ResponseEntity<JwtResponse>}
     */
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.authenticateUser(loginRequest), HttpStatus.OK);
    }

    /**
     * An API endpoint to to get new short time token.
     *
     * @param request {@link TokenRefreshRequest}
     * @return {@link ResponseEntity<TokenRefreshResponse>}
     */
    @PostMapping("/refresh/token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return new ResponseEntity<>(refreshTokenService.refreshToken(request), HttpStatus.OK);
    }

}
