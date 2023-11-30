package com.digigate.engineeringmanagement.common.service.impl;


import com.digigate.engineeringmanagement.common.entity.AccessRight;
import com.digigate.engineeringmanagement.common.converter.RoleConverter;
import com.digigate.engineeringmanagement.common.entity.Role;
import com.digigate.engineeringmanagement.common.payload.request.*;
import com.digigate.engineeringmanagement.common.repository.RoleRepository;
import com.digigate.engineeringmanagement.common.service.AccessRightService;
import com.digigate.engineeringmanagement.common.service.RoleService;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.SPACE_REGEX;

/**
 * Role service implementation
 *
 * @author Masud Rana
 */
@Service
public class RoleServiceImpl implements RoleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;
    private final AccessRightService accessRightService;
    private static final String UNDERSCORE = "_";
    private static final String SUCCESSFULLY_DELETION_MESSAGE = "Role Successfully Deleted";

    /**
     * Autowired constructor
     *
     * @param roleRepository        {@link RoleRepository}
     * @param accessRightService    {@link AccessRightService}
     */
    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, AccessRightService accessRightService) {
        this.roleRepository = roleRepository;
        this.accessRightService = accessRightService;
    }

    /**
     * responsible for finding specific role using role id
     *
     * @param id role id
     * @return role entity
     */
    @Override
    public Role findById(Integer id) {
        if (Objects.isNull(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.ID_IS_REQUIRED,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return roleRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> {
            throw new EngineeringManagementServerException(
                    ErrorId.ROLE_NOT_EXISTS,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID));
        });
    }

    /**
     * This method is responsible for getting role details by id
     *
     * @param id                            {@link Integer}
     * @return role details                 {@link  RoleDetailViewModel}
     */
    @Override
    public RoleDetailViewModel getDetailsById(Integer id) {
        Role role = findById(id);
        return new RoleDetailViewModel(role.getId(), role.getName());
    }

    /**
     * responsible for finding out specific user's access permission
     *
     * @param roleId role id of user
     * @return map of access right
     */
    @Override
    public Map<String, Integer> getRoleAccessPermission(Integer roleId) {
        Role role = this.findById(roleId);
        Set<AccessRight> accessRightSet = role.getAccessRightSet();
        Map<String, Integer> roleAccessMap = new HashMap<>();

        for (AccessRight accessRight : accessRightSet) {
            try {
                String key = StringUtils.upperCase(
                        accessRight.getConfigSubmoduleItem().getSubModule().getModule().getModuleName()
                        .replaceAll(SPACE_REGEX, UNDERSCORE))
                        + UNDERSCORE
                        + StringUtils.upperCase(accessRight.getConfigSubmoduleItem().getSubModule().getSubmoduleName()
                        .replaceAll(SPACE_REGEX, UNDERSCORE))
                        + UNDERSCORE
                        + StringUtils.upperCase(accessRight.getConfigSubmoduleItem().getItemName()
                        .replaceAll(SPACE_REGEX, UNDERSCORE))
                        + UNDERSCORE
                        + StringUtils.upperCase(accessRight.getAction().getActionName()
                        .replaceAll(SPACE_REGEX, UNDERSCORE));
                roleAccessMap.put(key, accessRight.getId());
            } catch (Exception ex) {
                LOGGER.error("Can't prepare key of access right. Exception: {}", ex.getMessage());
            }
        }

        return roleAccessMap;
    }

    /**
     * This method is responsible for saving role
     *
     * @param roleDto                       {@link RoleDto}
     * @return newly saved role id          {@link  Integer}
     */
    @Override
    public Integer save(RoleDto roleDto, Integer id) {

        if(roleRepository.existsByName(roleDto.getName())){
            LOGGER.error("Role name already exists");
            throw new EngineeringManagementServerException(
                    ErrorId.ROLE_NAME_ALREADY_EXISTS,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        Role role = new Role();
        if( Objects.isNull(id) ){
            role = RoleConverter.dtoToEntity(roleDto, new Role());
        }
        else{
            role = findById(id);
            role.setName(roleDto.getName());
        }

        try {
            return roleRepository.save(role).getId();
        } catch (Exception e){
            LOGGER.error("Role not saved : {}",roleDto);
            throw new EngineeringManagementServerException(
                    ErrorId.ROLE_NOT_SAVED,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }



    /**
     * This method is responsible for duplicating role
     *
     * @param roleDto                                   {@link RoleDto}
     * @param id                                        {@link Integer}
     * @return newly duplicated role id                 {@link Integer}
     */
    @Override
    public Integer duplicate(RoleDto roleDto, Integer id) {

        if(roleRepository.existsByName(roleDto.getName())){
            LOGGER.error("Role name already exist.");
            throw new EngineeringManagementServerException(
                    ErrorId.ROLE_NAME_ALREADY_EXISTS,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        Role role = findById(id);
        Role duplicateRole = new Role();

        duplicateRole.setName(roleDto.getName());
        duplicateRole.setIsDeleted(role.getIsDeleted());
        if (role.getAccessRightSet().isEmpty() == Boolean.FALSE){
            duplicateRole.setAccessRightSet(role.getAccessRightSet());
        }

        try {
            return roleRepository.save(duplicateRole).getId();
        }catch (Exception e){
            throw new EngineeringManagementServerException(
                    ErrorId.ROLE_DELETION_HAS_FAILED,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    /**
     * responsible for assigning/updating access right of a role
     *
     * @param roleAccessDto {@link RoleAccessDto}
     * @return role access right as view model
     */
    @Override
    public RoleViewModel updateRoleWithAccessRights(RoleAccessDto roleAccessDto) {
        Set<Integer> accessRightIds = roleAccessDto.getAccessRightIds();
        Role role = this.findById(roleAccessDto.getRoleId());

        if (CollectionUtils.isEmpty(accessRightIds)) {
            return new RoleViewModel(role.getId(),
                    role.getAccessRightSet().stream()
                            .map(AccessRight::getId)
                            .collect(Collectors.toSet()));
        }

        Set<AccessRight> accessRightSet = accessRightService.findAllAccessRightsByIds(accessRightIds);

        if (CollectionUtils.isNotEmpty(role.getAccessRightSet())) {
            role.getAccessRightSet().clear();
        }

        try {
            role.setAccessRightSet(accessRightSet);
            Role updatedRole = roleRepository.save(role);
            Set<Integer> accessRightsId = updatedRole.getAccessRightSet().stream()
                    .map(AccessRight::getId)
                    .collect(Collectors.toSet());

            ApplicationConstant.roleMap.put(role.getId(), accessRightsId);
            return new RoleViewModel(updatedRole.getId(), accessRightsId);
        } catch (Exception ex) {
            throw new EngineeringManagementServerException(
                    ErrorId.FAILED_TO_UPDATE_ROLE_ACCESS_RIGHTS, HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    /**
     * responsible for viewing role access rights
     *
     * @param roleId role id
     * @return role access right as view model
     */
    @Override
    public Map<Long, List<Integer>> getRoleAccessRights(Integer roleId) {
        Role role = this.findById(roleId);
        Set<AccessRight> accessRightSet = role.getAccessRightSet();
        Map<Long, List<Integer>> roleAccessRightMap = new HashMap<>();

        for (AccessRight accessRight : accessRightSet) {
            Long moduleId = accessRight.getConfigSubmoduleItem().getSubModule().getModule().getId();
            if (roleAccessRightMap.containsKey(moduleId)) {
                roleAccessRightMap.get(moduleId).add(accessRight.getId());
            } else {
                List<Integer> accessList = new ArrayList<>();
                accessList.add(accessRight.getId());
                roleAccessRightMap.put(moduleId, accessList);
            }
        }
        return roleAccessRightMap;
    }

    /**
     * This method is responsible for deleting a role
     *
     * @param id                                    {@link Integer}
     * @return successfully deleted message         {@link String}
     */
    @Override
    public String delete(Integer id) {

        Role role = this.findById(id);

        if(CollectionUtils.isNotEmpty(role.getUserSet())){
            LOGGER.error("Role deletion is not possible with id {}", id);
            throw new EngineeringManagementServerException(
                    ErrorId.ROLE_DELETION_HAS_FAILED,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        role.setIsDeleted(Boolean.TRUE);

        try {
            roleRepository.save(role);
            return SUCCESSFULLY_DELETION_MESSAGE;
        }catch (Exception e){
            LOGGER.error("Role deletion is not possible: {} ",id);
            throw new EngineeringManagementServerException(
                    ErrorId.ROLE_DELETION_HAS_FAILED,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

    }

    /**
     * This method is responsible for getting all roles
     *
     * @return list of roles        {@link RoleDetailViewModel}
     */
    @Override
    public List<RoleDetailViewModel> getAll() {
        try {
            return roleRepository.findAllActiveRole();
        }catch (Exception e){
            LOGGER.error("Role's not found!");
            throw new EngineeringManagementServerException(
                    ErrorId.ROLE_NOT_EXISTS,
                    HttpStatus.NOT_FOUND,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }
}
