package com.digigate.engineeringmanagement.common.authentication.service;


import com.digigate.engineeringmanagement.common.authentication.entity.RefreshToken;
import com.digigate.engineeringmanagement.common.authentication.payload.request.TokenRefreshRequest;
import com.digigate.engineeringmanagement.common.authentication.payload.response.TokenRefreshResponse;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(Long userId);

    RefreshToken verifyExpiration(RefreshToken token);

    int deleteByUserId(Long userId);

    TokenRefreshResponse refreshToken(TokenRefreshRequest request);
}
