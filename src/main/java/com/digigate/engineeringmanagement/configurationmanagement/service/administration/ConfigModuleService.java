package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.AccessRight;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.*;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration.ConfigModuleDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.ConfigModuleResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigModule;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigSubModule;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigSubmoduleItem;
import com.digigate.engineeringmanagement.configurationmanagement.repository.administration.ConfigModuleRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.ConfigMenuSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.ConfigMenuSearchDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static java.lang.Boolean.FALSE;

/**
 * Configuration module service
 */
@Service
public class ConfigModuleService implements IModuleService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ConfigModuleService.class);
    private final ConfigModuleRepository moduleRepository;
    private final ISubModuleService subModuleService;

    public ConfigModuleService(ConfigModuleRepository moduleRepository,
                               @Lazy ISubModuleService subModuleService) {

        this.moduleRepository = moduleRepository;
        this.subModuleService = subModuleService;
    }

    /**
     * This method is responsible for validating unique module name and updating module
     *
     * @param dto {@link ConfigModuleDto}
     * @param id  {@link ConfigModule}
     * @return responding config module {@link ConfigModule}
     */
    public ConfigModule update(ConfigModuleDto dto, Long id) {
        ConfigModule configModule = findByIdUnfiltered(id);
        validate(dto, configModule);
        final ConfigModule convertConfigModule = updateEntity(dto, configModule);
        return saveItem(convertConfigModule);
    }

    @Override
    public ConfigModuleResponseDto getSingle(Long id) {
        return convertToResponseDto(moduleRepository.findById(id).orElseThrow(() ->
            EngineeringManagementServerException.notFound(Helper.createDynamicCode(ErrorId.DATA_NOT_FOUND_DYNAMIC, "Module"))));
    }

    /**
     * This method will return Active or Deactivated Entity
     *
     * @param id {@link Long
     */
    public ConfigModule findByIdUnfiltered(Long id) {
        return findOptionalById(id, false)
            .orElseThrow(() -> EngineeringManagementServerException.notFound(
                Helper.createDynamicCode(ErrorId.NOT_FOUND_DYNAMIC, MODULE)));
    }

    private Optional<ConfigModule> findOptionalById(Long id, boolean activeRequired) {
        if (ObjectUtils.isEmpty(id)) {
            throw EngineeringManagementServerException.notFound(
                Helper.createDynamicCode(ErrorId.NOT_FOUND_DYNAMIC, MODULE));
        }
        return activeRequired ? moduleRepository.findByIdAndIsActiveTrue(id) : moduleRepository.findById(id);
    }

    private ConfigModule saveItem(ConfigModule entity) {
        try {
            return moduleRepository.save(entity);
        } catch (Exception e) {
            String name = entity.getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", name);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(
                Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    name));
        }
    }


    /**
     * This method is responsible for checking has child and toggling module status
     *
     * @param id       {@link ConfigModule}
     * @param isActive {@link Boolean}
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == FALSE && subModuleService.isModuleInActivePossible(id)) {
            throw new EngineeringManagementServerException(
                ErrorId.MODULE_INACTIVE_IS_NOT_POSSIBLE,
                HttpStatus.PRECONDITION_FAILED,
                MDC.get(TRACE_ID)
            );
        }
        ConfigModule e = findByIdUnfiltered(id);
        if (Objects.equals(e.getIsActive(), isActive)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }
        e.setIsActive(isActive);
        saveItem(e);
    }

    private ConfigModuleResponseDto convertToResponseDto(ConfigModule module) {
        return ConfigModuleResponseDto.builder()
            .id(module.getId())
            .moduleName(module.getModuleName())
            .image(module.getImage())
            .order(module.getOrder())
            .isActive(module.getIsActive())
            .build();
    }

    public ConfigModule updateEntity(ConfigModuleDto dto, ConfigModule entity) {
        return buildModule(dto, entity);
    }

    @Override
    public List<ModuleViewModel> getAllModule() {
        List<ConfigModule> moduleList = moduleRepository.findAllByIsActiveTrue();
        List<ModuleViewModel> moduleViewModelList = new ArrayList<>();
        for (ConfigModule module : moduleList) {
            ModuleViewModel moduleViewModel = new ModuleViewModel();
            moduleViewModel.setModuleId(module.getId());
            moduleViewModel.setModuleName(module.getModuleName());
            moduleViewModel.setImage(module.getImage());
            module.setOrder(module.getOrder());

            List<ConfigSubModule> subModuleList = module.getSubModuleList();
            List<SubModuleViewModel> subModuleViewModelList = new ArrayList<>();
            for (ConfigSubModule subModule : subModuleList) {
                SubModuleViewModel subModuleViewModel = new SubModuleViewModel();
                subModuleViewModel.setSubModuleId(subModule.getId());
                subModuleViewModel.setSubModuleName(subModule.getSubmoduleName());
                subModuleViewModel.setOrder(subModule.getOrder());

                List<ConfigSubmoduleItem> featureList = subModule.getSubmoduleItems();
                List<FeatureViewModel> featureViewModelList = new ArrayList<>();
                for (ConfigSubmoduleItem feature : featureList) {
                    FeatureViewModel featureViewModel = new FeatureViewModel();

                    featureViewModel.setFeatureId(feature.getId());
                    featureViewModel.setFeatureName(feature.getItemName());
                    featureViewModel.setOrder(feature.getOrder());
                    featureViewModel.setUrlPath(feature.getUrlPath());
                    featureViewModel.setIsBase(feature.getIsBase());

                    Set<AccessRight> accessRightList = feature.getAccessRightSet();
                    List<ActionViewModel> actionViewModelList = new ArrayList<>();

                    for (AccessRight accessRight : accessRightList) {
                        ActionViewModel actionViewModel = new ActionViewModel();
                        actionViewModel.setActionId(accessRight.getAction().getId());
                        actionViewModel.setActionName(accessRight.getAction().getActionName());
                        actionViewModel.setAccessRightId(accessRight.getId());
                        actionViewModelList.add(actionViewModel);
                    }

                    featureViewModel.setActionViewModelList(actionViewModelList);
                    featureViewModelList.add(featureViewModel);
                }

                subModuleViewModel.setFeatureViewModelList(featureViewModelList);
                subModuleViewModelList.add(subModuleViewModel);
            }

            moduleViewModel.setSubModuleList(subModuleViewModelList);
            moduleViewModelList.add(moduleViewModel);
        }

        return moduleViewModelList;
    }

    @Override
    public List<ConfigModule> saveItemList(List<ConfigModule> entityList) {
        try {
            if (CollectionUtils.isEmpty(entityList)) {
                return entityList;
            }
//            if (moduleRepository.findById(entityList.get(FIRST_INDEX).getId()).isPresent()) {
//                return Collections.emptyList();
//            }
            return moduleRepository.saveAll(entityList);
        } catch (Exception e) {
            String entityName = entityList.get(0).getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", entityName);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                entityName));
        }
    }

    private Specification<ConfigModule> likeSpecificationAtRoot(String value, String columnName) {
        return (Root<ConfigModule> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (StringUtils.isNotBlank(value)) {
                return criteriaBuilder.like(root.get(columnName), value + "%");
            }
            return null;
        };
    }

    /**
     * search entity by criteria
     *
     * @param searchDto {@link IdQuerySearchDto}
     * @param pageable  {@link Pageable}
     */
    @Override
    public PageData search(ConfigMenuSearchDto searchDto, Pageable pageable) {
        Specification<ConfigModule> customSpecification = buildSpecification(searchDto).and(new CustomSpecification()
            .active(Objects.nonNull(searchDto.getIsActive()) ? searchDto.getIsActive() : true, IS_ACTIVE_FIELD));
        Page<ConfigModule> pagedData = moduleRepository.findAll(customSpecification, pageable);
        List<Object> models = pagedData.getContent()
            .stream().map(this::convertToResponseDto).collect(Collectors.toList());
        return PageData.builder()
            .model(models)
            .totalPages(pagedData.getTotalPages())
            .totalElements(pagedData.getTotalElements())
            .currentPage(pageable.getPageNumber() + 1)
            .build();
    }

    private Specification<ConfigModule> buildSpecification(IdQuerySearchDto searchDto) {
        return Specification.where(
            likeSpecificationAtRoot(searchDto.getQuery(), CONFIG_MODULE_NAME));
    }

    private ConfigModule buildModule(ConfigModuleDto moduleDto, ConfigModule module) {
        module.setModuleName(moduleDto.getModuleName());
        module.setImage(moduleDto.getImage());
        module.setOrder(moduleDto.getOrder());
        module.setIsActive(Objects.isNull(moduleDto.getIsActive()) ? Boolean.TRUE : moduleDto.getIsActive());
        return module;
    }

    /**
     * This method is responsible for validating case-sensitive module name
     *
     * @param configModuleDto {@link ConfigModuleDto}
     * @param old             {@link ConfigModule}
     */
    private void validate(ConfigModuleDto configModuleDto, ConfigModule old) {
        List<ConfigModule> moduleList = moduleRepository.findByModuleNameIgnoreCase(configModuleDto.getModuleName());

        if (CollectionUtils.isNotEmpty(moduleList) && (
            Objects.isNull(old) ||
                moduleList.size() > VALUE_ONE ||
                !moduleList.get(FIRST_INDEX).equals(old))) {
            throw EngineeringManagementServerException.badRequest(
                ErrorId.MODULE_NAME_ALREADY_EXIST);
        }
    }
    /**
     * responsible for getting all active module
     *
     * @return list of ConfigModule
     */
}
