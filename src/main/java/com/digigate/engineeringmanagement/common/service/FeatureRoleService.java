package com.digigate.engineeringmanagement.common.service;

import com.digigate.engineeringmanagement.common.payload.request.FeatureRolePayload;
import com.digigate.engineeringmanagement.common.payload.response.FeatureRoleViewModel;

public interface FeatureRoleService {
    int assign(FeatureRolePayload payload);

    FeatureRoleViewModel featuresByRoleId(Long id);
}
