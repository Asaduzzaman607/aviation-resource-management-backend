package com.digigate.engineeringmanagement.common.service.auth;

public interface AuthUserService {
    Long getAuthId();
    boolean matchesAuthId(Long id);
    void validateUserId(Long userId);
}
