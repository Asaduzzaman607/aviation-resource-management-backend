package com.digigate.engineeringmanagement.common.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleViewModel {
    private Integer roleId;
    Set<Integer> roleAccessAccessRights;
}
