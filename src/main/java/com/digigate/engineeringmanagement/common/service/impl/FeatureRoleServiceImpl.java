package com.digigate.engineeringmanagement.common.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.FeatureType;
import com.digigate.engineeringmanagement.common.entity.FeatureRole;
import com.digigate.engineeringmanagement.common.entity.Role;
import com.digigate.engineeringmanagement.common.payload.request.FeatureRolePayload;
import com.digigate.engineeringmanagement.common.payload.response.FeatureRoleViewModel;
import com.digigate.engineeringmanagement.common.repository.FeatureRoleRepository;
import com.digigate.engineeringmanagement.common.service.FeatureRoleService;
import com.digigate.engineeringmanagement.common.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class FeatureRoleServiceImpl implements FeatureRoleService {

    private final RoleService roleService;
    private final FeatureRoleRepository repository;
    private final List<FeatureRole> featureRoles;

    @Autowired
    public FeatureRoleServiceImpl(RoleService roleService, FeatureRoleRepository featureRoleRepository) {
        this.roleService = roleService;
        repository = featureRoleRepository;
        featureRoles = new ArrayList<>();
    }

    @Override
    @Transactional
    public int assign(FeatureRolePayload payload) {
        Role role = getRole(payload.getRoleId());
        prepareEntities(payload, role);
        List<FeatureRole> featureRoleList = repository.findAllByRole(role);
        repository.deleteAllInBatch(featureRoleList);
        repository.saveAll(featureRoles);
        return ApplicationConstant.DEFAULT_SUCCESS_RESPONSE;
    }

    @Override
    public FeatureRoleViewModel featuresByRoleId(Long id) {
        Role role = getRole(id.intValue());
        List<FeatureRole> allByRole = repository.findAllByRole(role);
        return convertToViewModel(allByRole);
    }

    private void prepareEntities(FeatureRolePayload payload, Role role) {
        prepareTypeWiseEntities(payload.getModuleIds(), FeatureType.MODULE, role);
        prepareTypeWiseEntities(payload.getSubModuleIds(), FeatureType.SUB_MODULE, role);
        prepareTypeWiseEntities(payload.getSubModuleItemIds(), FeatureType.SUB_MODULE_ITEM, role);
    }

    private void prepareTypeWiseEntities(Set<Long> featureIDs, FeatureType featureType, Role role) {
        for (Long featureID : featureIDs) {
            FeatureRole featureRole = new FeatureRole();
            featureRole.setRole(role);
            featureRole.setFeatureId(featureID);
            featureRole.setFeatureType(featureType);
            featureRoles.add(featureRole);
        }
    }

    private Role getRole(Integer roleId) {
        return roleService.findById(roleId);
    }

    private FeatureRoleViewModel convertToViewModel(List<FeatureRole> allByRole) {
        FeatureRoleViewModel viewModel = new FeatureRoleViewModel();
        viewModel.setModuleIds(getFeatureIdsByType(allByRole, FeatureType.MODULE));
        viewModel.setSubModuleIds(getFeatureIdsByType(allByRole, FeatureType.SUB_MODULE));
        viewModel.setSubModuleItemIds(getFeatureIdsByType(allByRole, FeatureType.SUB_MODULE_ITEM));
        return viewModel;
    }

    private Set<Long> getFeatureIdsByType(List<FeatureRole> features, FeatureType featureType) {
        return features.stream()
                .filter(matches(featureType))
                .map(FeatureRole::getFeatureId)
                .collect(Collectors.toSet());
    }

    private Predicate<FeatureRole> matches(FeatureType featureType) {
        return featureRole -> featureRole.getFeatureType().equals(featureType);
    }
}
