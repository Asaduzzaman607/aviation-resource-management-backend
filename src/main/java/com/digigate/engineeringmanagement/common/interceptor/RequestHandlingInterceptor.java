package com.digigate.engineeringmanagement.common.interceptor;

import com.digigate.engineeringmanagement.common.authentication.security.jwt.JwtUtils;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.AccessRight;
import com.digigate.engineeringmanagement.common.entity.Role;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.loader.DefaultAccessRightLoader;
import com.digigate.engineeringmanagement.common.service.UserService;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Request Handling Interceptor
 *
 * @author Pranoy Das
 */
@Component
public class RequestHandlingInterceptor implements HandlerInterceptor {

    private static final String SLASH = "/";
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final Helper helper;
    /**
     * Autowired constructor
     *
     * @param userService {@link UserService}
     * @param jwtUtils    {@link JwtUtils}
     * @param helper      {@link Helper}
     */
    @Autowired
    public RequestHandlingInterceptor(@Lazy UserService userService, JwtUtils jwtUtils, Helper helper) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.helper = helper;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler) {
        MDC.put(ApplicationConstant.TRACE_ID, UUID.randomUUID().toString());
        String requestURI = helper.getRequestUri();

        if (!includesInWhiteListedUrls(requestURI)) {
            String jwt = helper.parseJwt();
            String username = jwtUtils.getUserNameFromJwtToken(jwt);

            Map pathVariables =(Map) request.getAttribute(
                    HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            String methodType = request.getMethod();

            User user = userService.findByLogin(username);
            validateUserAccessPermission(user, requestURI, pathVariables, methodType);
        }

        return true;
    }

    private boolean includesInWhiteListedUrls(String path) {
        return Arrays.stream(ApplicationConstant.WHITE_LIST_URLS)
                .anyMatch(path::startsWith);
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) {
    }

    private void validateUserAccessPermission(User user, String requestURI, Map pathVariables, String methodType) {
        Role role = user.getRole();

        if (Objects.equals(role.getId(), ApplicationConstant.SUPER_ADMIN_ROLE_ID)) {
            return;
        }

        String[] requestURIArr = requestURI.split(SLASH);
        int length = pathVariables.size();
        int requestURIArrLastIndex = requestURIArr.length - 1;

        while (length > 0 && requestURIArr.length > length) {
            requestURIArr = ArrayUtils.remove(requestURIArr, requestURIArrLastIndex);
            length--;
            requestURIArrLastIndex--;
        }

        StringBuilder sb = new StringBuilder();
        for (String s : requestURIArr) {
            sb.append(s).append(SLASH);
        }
        sb.append(methodType.toLowerCase());

        Set<Integer> accessRightSet;
        if (ApplicationConstant.roleMap.containsKey(role.getId())) {
            accessRightSet = ApplicationConstant.roleMap.get(role.getId());
        } else {
            accessRightSet = user.getRole().getAccessRightSet().stream()
                    .map(AccessRight::getId)
                    .collect(Collectors.toSet());
            ApplicationConstant.roleMap.put(role.getId(), accessRightSet);
        }

        Integer accessRightId = NumberUtil.convertToInteger(
                DefaultAccessRightLoader.DEFAULT_ACCESS_MAP.get(sb.toString()),
                ApplicationConstant.DEFAULT_PERMISSION_ID
        );

        if (Objects.equals(accessRightId, ApplicationConstant.DEFAULT_PERMISSION_ID)) {
            return;
        }

        if (!accessRightSet.contains(accessRightId)) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_ACCESS_PERMISSION, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }
}
