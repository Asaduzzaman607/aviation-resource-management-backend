package com.digigate.engineeringmanagement.common.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
public class FeatureRolePayload {
    private Long id;

    private Integer roleId;

    @NotEmpty
    private Set<Long> moduleIds;

    @NotEmpty
    private Set<Long> subModuleIds;

    @NotEmpty
    private Set<Long> subModuleItemIds;
}
