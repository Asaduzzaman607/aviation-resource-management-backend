package com.digigate.engineeringmanagement.common.authentication.service.impl;


import com.digigate.engineeringmanagement.common.authentication.entity.RefreshToken;
import com.digigate.engineeringmanagement.common.authentication.payload.request.LoginRequest;
import com.digigate.engineeringmanagement.common.authentication.payload.response.JwtResponse;
import com.digigate.engineeringmanagement.common.authentication.security.jwt.JwtUtils;
import com.digigate.engineeringmanagement.common.authentication.security.services.UserDetailsImpl;
import com.digigate.engineeringmanagement.common.authentication.service.AuthService;
import com.digigate.engineeringmanagement.common.authentication.service.RefreshTokenService;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.FeatureRoleViewModel;
import com.digigate.engineeringmanagement.common.payload.response.ModuleViewModel;
import com.digigate.engineeringmanagement.common.service.FeatureRoleService;
import com.digigate.engineeringmanagement.common.service.RoleService;
import com.digigate.engineeringmanagement.common.service.UserService;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.IModuleService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IModuleService configModuleService;
    private final RoleService roleService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final FeatureRoleService featureRoleService;

    /**
     * Autowired constructor
     *
     *  @param authenticationManager {@link AuthenticationManager}
     * @param jwtUtils              {@link JwtUtils}
     * @param roleService           {@link RoleService}
     * @param userService           {@link UserService}
     * @param refreshTokenService   {@link RefreshTokenService}
     * @param featureRoleService   {@link FeatureRoleService}
     */
    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtUtils jwtUtils,
                           RoleService roleService,
                           UserService userService,
                           RefreshTokenService refreshTokenService,
                           IModuleService configModuleService,
                           FeatureRoleService featureRoleService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.configModuleService = configModuleService;
        this.roleService = roleService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.featureRoleService = featureRoleService;
    }

    /**
     * validate user login credential and generate token
     *
     * @param loginRequest {@link LoginRequest}
     * @return {@link JwtResponse}
     */
    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        if (!jwtUtils.validateJwtToken(jwt)) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_JWT_TOKEN,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(roles)) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_USER, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
        }

        List<ModuleViewModel> moduleViewModelList = configModuleService.getAllModule();
        Map<String, Integer> userAccessPermissions =
                roleService.getRoleAccessPermission(NumberUtil.convertToInteger(roles.get(0)));
        FeatureRoleViewModel featureRoleViewModel = featureRoleService.featuresByRoleId(userDetails.getRoleId().longValue());
        return new JwtResponse(
                jwt,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                roles,
                moduleViewModelList,
                userAccessPermissions,
                featureRoleViewModel
        );
    }
}
