package com.digigate.engineeringmanagement.common.payload.response;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class FeatureRoleViewModel {
    private Set<Long> moduleIds;
    private Set<Long> subModuleIds;
    private Set<Long> subModuleItemIds;
}
