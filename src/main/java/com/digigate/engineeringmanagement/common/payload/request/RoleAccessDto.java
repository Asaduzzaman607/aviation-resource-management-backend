package com.digigate.engineeringmanagement.common.payload.request;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAccessDto {
    @NotNull(message = ErrorId.ROLE_IS_REQUIRED)
    private Integer roleId;
    private Set<Integer> accessRightIds;
}
