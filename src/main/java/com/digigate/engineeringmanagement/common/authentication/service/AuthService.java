package com.digigate.engineeringmanagement.common.authentication.service;


import com.digigate.engineeringmanagement.common.authentication.payload.request.LoginRequest;
import com.digigate.engineeringmanagement.common.authentication.payload.response.JwtResponse;

public interface AuthService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
}
