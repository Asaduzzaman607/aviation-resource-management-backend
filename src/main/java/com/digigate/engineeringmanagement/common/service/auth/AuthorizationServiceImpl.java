package com.digigate.engineeringmanagement.common.service.auth;

import com.digigate.engineeringmanagement.common.authentication.security.services.UserDetailsImpl;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {
    @Override
    public void validateSuperAdmin() {
        if (!isSuperAdmin()) {
            throw EngineeringManagementServerException.notAuthorized(ErrorId.NOT_SUPER_ADMIN);
        }
    }

    @Override
    public void validateUserId(Long userId) {
        if (!matchesAuthId(userId)) {
            throw EngineeringManagementServerException.notAuthorized(ErrorId.NOT_VALID_USER);
        }
    }

    @Override
    public boolean isSuperAdmin() {
        return matchesRoleId(ApplicationConstant.SUPER_ADMIN_ROLE_ID);
    }

    @Override
    public boolean matchesRoleId(int roleId) {
        return Objects.equals(getRoleId(), roleId);
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public int getRoleId() {
        Authentication authentication = getAuthentication();
        return ((UserDetailsImpl) authentication.getPrincipal()).getRoleId();
    }

    @Override
    public Long getAuthId() {
        Authentication authentication = getAuthentication();
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }

    @Override
    public boolean matchesAuthId(Long id) {
        return Objects.equals(getAuthId(), id);
    }

}
