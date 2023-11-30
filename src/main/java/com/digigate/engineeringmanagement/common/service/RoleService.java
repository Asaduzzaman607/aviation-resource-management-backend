package com.digigate.engineeringmanagement.common.service;


import com.digigate.engineeringmanagement.common.entity.Role;
import com.digigate.engineeringmanagement.common.payload.request.RoleAccessDto;
import com.digigate.engineeringmanagement.common.payload.request.RoleDetailViewModel;
import com.digigate.engineeringmanagement.common.payload.request.RoleDto;
import com.digigate.engineeringmanagement.common.payload.request.RoleViewModel;

import java.util.List;
import java.util.Map;

public interface RoleService {
    Role findById(Integer id);
    RoleDetailViewModel getDetailsById(Integer id);
    Integer save(RoleDto roleDto, Integer id);
    Map<String, Integer> getRoleAccessPermission(Integer roleId);
    Integer duplicate(RoleDto roleDto, Integer id);
    RoleViewModel updateRoleWithAccessRights(RoleAccessDto roleAccessDto);
    Map<Long, List<Integer>> getRoleAccessRights(Integer roleId);
    String delete(Integer id);
    List<RoleDetailViewModel> getAll();

}
