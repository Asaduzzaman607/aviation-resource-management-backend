package com.digigate.engineeringmanagement.common.service.auth;

public interface AuthRoleService {
    int getRoleId();
    boolean matchesRoleId(int roleId);
}
