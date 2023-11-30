package com.digigate.engineeringmanagement.common.converter;

import com.digigate.engineeringmanagement.common.entity.Role;
import com.digigate.engineeringmanagement.common.payload.request.RoleDto;

import java.util.Objects;

public class RoleConverter {
    /**
     * This static method responsible for converting role dto to role entity
     * @param roleDto {@link RoleDto}
     * @param role {@link  Role}
     * @return role {@link Role}
     */
    public static Role dtoToEntity(RoleDto roleDto, Role role){
        role.setName(roleDto.getName());
        return role;
    }
}
