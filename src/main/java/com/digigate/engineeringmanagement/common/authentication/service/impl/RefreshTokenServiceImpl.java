package com.digigate.engineeringmanagement.common.authentication.service.impl;

import com.digigate.engineeringmanagement.common.authentication.entity.RefreshToken;
import com.digigate.engineeringmanagement.common.authentication.payload.request.TokenRefreshRequest;
import com.digigate.engineeringmanagement.common.authentication.payload.response.TokenRefreshResponse;
import com.digigate.engineeringmanagement.common.authentication.repository.RefreshTokenRepository;
import com.digigate.engineeringmanagement.common.authentication.security.jwt.JwtUtils;
import com.digigate.engineeringmanagement.common.authentication.service.RefreshTokenService;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.UserService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${digigate.app.refresh.jwtExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    /**
     * Parameterized constructor
     *  @param refreshTokenRepository {@link RefreshTokenRepository}
     * @param userService {@link UserService}
     * @param jwtUtils        {@link JwtUtils}
     */
    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserService userService, JwtUtils jwtUtils) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * find by login or throw error
     *
     * @param token {@link String}
     * @return {@link Optional<RefreshToken>}
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * create refresh token
     *
     * @param userId {@link String}
     * @return {@link RefreshToken}
     */
    public RefreshToken createRefreshToken(Long userId) {

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserId(userId);
        RefreshToken refreshToken;
        if (optionalRefreshToken.isPresent()) {
            refreshToken = optionalRefreshToken.get();
        } else {
            refreshToken = new RefreshToken();
        }
        refreshToken.setUser(userService.findUserById(userId));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    /**
     * verify refresh token expiration
     *
     * @param token {@link String}
     * @return {@link RefreshToken}
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new EngineeringManagementServerException(
                    ErrorId.REFRESH_TOKEN_WAS_EXPIRED,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return token;
    }

    /**
     * delete refresh token by userId
     *
     * @param userId {@link String}
     * @return {@link RefreshToken}
     */
    @Transactional
    @Override
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * create refresh token
     *
     * @param request {@link TokenRefreshRequest}
     * @return {@link TokenRefreshResponse}
     */
    @Override
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getLogin());
                    return new TokenRefreshResponse(token, requestRefreshToken);
                })
                .orElseThrow(() -> new EngineeringManagementServerException(
                        ErrorId.REFRESH_TOKEN_WAS_EXPIRED,
                        HttpStatus.NOT_FOUND,
                        MDC.get(ApplicationConstant.TRACE_ID)));
    }

}