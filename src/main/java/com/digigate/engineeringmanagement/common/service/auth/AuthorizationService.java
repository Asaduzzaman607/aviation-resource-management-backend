package com.digigate.engineeringmanagement.common.service.auth;

public interface AuthorizationService extends AuthRoleService, AuthUserService {
    boolean isSuperAdmin();
    void validateSuperAdmin();
}
