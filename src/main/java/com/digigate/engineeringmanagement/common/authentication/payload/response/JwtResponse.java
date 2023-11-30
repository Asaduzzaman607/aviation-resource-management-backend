package com.digigate.engineeringmanagement.common.authentication.payload.response;

import com.digigate.engineeringmanagement.common.payload.response.FeatureRoleViewModel;
import com.digigate.engineeringmanagement.common.payload.response.ModuleViewModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Long id;
    private String username;
    private List<String> roles;
    private List<ModuleViewModel> defaultAccessRight;
    private Map<String, Integer> userAccessPermissions;
    private FeatureRoleViewModel featureRoleViewModel;

    public JwtResponse(String accessToken, String refreshToken, Long id, String username, List<String> roles,
                       List<ModuleViewModel> defaultAccessRight, Map<String, Integer> userAccessPermissions, FeatureRoleViewModel featureRole) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.defaultAccessRight = defaultAccessRight;
        this.userAccessPermissions = userAccessPermissions;
        featureRoleViewModel = featureRole;
    }
}
