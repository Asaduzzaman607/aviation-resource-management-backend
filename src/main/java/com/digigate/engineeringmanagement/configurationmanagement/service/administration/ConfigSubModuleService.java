package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration.ConfigSubModuleDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.ConfigSubModuleResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigModule;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigSubModule;
import com.digigate.engineeringmanagement.configurationmanagement.repository.administration.ConfigSubModuleRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.ConfigMenuSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.ConfigMenuSearchDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static java.lang.Boolean.FALSE;


@Service
public class ConfigSubModuleService implements ISubModuleService {
    private final ConfigSubModuleRepository subModuleRepository;
    private final IModuleService moduleService;
    private final ISubModuleItemService itemService;

    protected static final Logger LOGGER = LoggerFactory.getLogger(ConfigSubModuleService.class);

    public ConfigSubModuleService(ConfigSubModuleRepository subModuleRepository,
                                  IModuleService moduleService,
                                  @Lazy ISubModuleItemService itemService) {
        this.subModuleRepository = subModuleRepository;
        this.moduleService = moduleService;
        this.itemService = itemService;
    }

    /**
     * This method is responsible for validating unique submodule name under same module and updating submodule
     *
     * @param configSubModuleDto {@link ConfigSubModuleDto}
     * @param id                 {@link ConfigSubModule}
     * @return responding config submodule{@link ConfigSubModule}
     */
    public ConfigSubModule update(ConfigSubModuleDto configSubModuleDto, Long id) {
        ConfigSubModule configSubModule = findByIdUnfiltered(id);
        validate(configSubModuleDto, configSubModule);
        final ConfigSubModule convertedSubModule = updateEntity(configSubModuleDto, configSubModule);
        return saveItem(convertedSubModule);
    }

    @Override
    public ConfigSubModuleResponseDto getSingle(Long id) {
        return convertToResponseDto(findByIdUnfiltered(id));
    }

    /**
     * This method is responsible for checking has child and toggling submodule status
     *
     * @param id       {@link ConfigSubModule}
     * @param isActive {@link Boolean}
     */
    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == FALSE && itemService.isPossibleInActiveSubmodule(id)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.SUB_MODULE_INACTIVE_IS_NOT_POSSIBLE);
        }
        ConfigSubModule e = findByIdUnfiltered(id);
        if (Objects.equals(e.getIsActive(), isActive)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }
        e.setIsActive(isActive);
        saveItem(e);
    }

    /**
     * This method is responsible for checking parent existence
     *
     * @param moduleId {@link ConfigModule}
     * @return responding boolean data
     */
    @Override
    public boolean isModuleInActivePossible(Long moduleId) {
        return subModuleRepository.existsByModuleIdAndIsActiveTrue(moduleId);
    }

    public Specification<ConfigSubModule> likeSpecificationAtRoot(String value, String columnName) {
        return (Root<ConfigSubModule> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (StringUtils.isNotBlank(value)) {
                return criteriaBuilder.like(root.get(columnName), value + "%");
            }
            return null;
        };
    }

    @Override
    public List<ConfigSubModule> saveItemList(List<ConfigSubModule> entityList) {
        try {
            if (CollectionUtils.isEmpty(entityList)) {
                return entityList;
            }
//            if (subModuleRepository.findById(entityList.get(FIRST_INDEX).getId()).isPresent()) {
//                return Collections.emptyList();
//            }
            return subModuleRepository.saveAll(entityList);
        } catch (Exception e) {
            String entityName = entityList.get(0).getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", entityName);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(
                Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    entityName));
        }
    }

    /**
     * search entity by criteria
     *
     * @param searchDto {@link IdQuerySearchDto}
     * @param pageable  {@link Pageable}
     */
    public PageData search(ConfigMenuSearchDto searchDto, Pageable pageable) {
        Specification<ConfigSubModule> customSpecification = buildSpecification(searchDto).and(new CustomSpecification()
            .active(Objects.nonNull(searchDto.getIsActive()) ? searchDto.getIsActive() : true, IS_ACTIVE_FIELD));
        Page<ConfigSubModule> pagedData = subModuleRepository.findAll(customSpecification, pageable);
        List<Object> models = pagedData.getContent()
            .stream().map(this::convertToResponseDto).collect(Collectors.toList());
        return PageData.builder()
            .model(models)
            .totalPages(pagedData.getTotalPages())
            .totalElements(pagedData.getTotalElements())
            .currentPage(pageable.getPageNumber() + 1)
            .build();
    }

    protected ConfigSubModuleResponseDto convertToResponseDto(ConfigSubModule subModule) {
        ConfigModule module = subModule.getModule();
        return ConfigSubModuleResponseDto.builder()
            .id(subModule.getId())
            .moduleId(module.getId())
            .moduleName(module.getModuleName())
            .order(subModule.getOrder())
            .submoduleName(subModule.getSubmoduleName())
            .isActive(subModule.getIsActive())
            .build();
    }

    protected ConfigSubModule saveItem(ConfigSubModule entity) {
        try {
            return subModuleRepository.save(entity);
        } catch (Exception e) {
            String name = entity.getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", name);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(
                Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    name));
        }
    }

    @Override
    public ConfigSubModule findByIdUnfiltered(Long id) {
        return findOptionalById(id, false)
            .orElseThrow(() -> EngineeringManagementServerException.notFound(
                Helper.createDynamicCode(ErrorId.NOT_FOUND_DYNAMIC, SUB_MODULE)));
    }

    protected ConfigSubModule updateEntity(ConfigSubModuleDto configSubModuleDto, ConfigSubModule subModule) {
        subModule.setModule(moduleService.findByIdUnfiltered(configSubModuleDto.getModuleId()));
        return buildSubModule(configSubModuleDto, subModule);
    }

    protected Specification<ConfigSubModule> buildSpecification(IdQuerySearchDto searchDto) {
        return Specification.where(
            likeSpecificationAtRoot(searchDto.getQuery(), CONFIG_SUB_MODULE_NAME));
    }

    private ConfigSubModule buildSubModule(ConfigSubModuleDto configSubModuleDto, ConfigSubModule subModule) {
        subModule.setSubmoduleName(configSubModuleDto.getSubmoduleName());
        subModule.setOrder(configSubModuleDto.getOrder());
        subModule.setIsActive(
            Objects.isNull(configSubModuleDto.getIsActive()) ? Boolean.TRUE : configSubModuleDto.getIsActive());
        return subModule;
    }

    /**
     * This method is responsible for validating case-sensitive submodule name
     *
     * @param configSubModuleDto {@link ConfigSubModuleDto}
     * @param old                {@link ConfigSubModule}
     */
    private void validate(ConfigSubModuleDto configSubModuleDto, ConfigSubModule old) {
        List<ConfigSubModule> subModuleList =
            subModuleRepository.findByModuleIdAndSubmoduleNameIgnoreCase(
                configSubModuleDto.getModuleId(),
                configSubModuleDto.getSubmoduleName());

        if (CollectionUtils.isNotEmpty(subModuleList) && (
            Objects.isNull(old) ||
                subModuleList.size() > VALUE_ONE ||
                !subModuleList.get(FIRST_INDEX).equals(old))) {
            throw EngineeringManagementServerException.badRequest(
                ErrorId.SUB_MODULE_NAME_ALREADY_EXIST);
        }
    }

    private Optional<ConfigSubModule> findOptionalById(Long id, boolean activeRequired) {
        if (ObjectUtils.isEmpty(id)) {
            throw EngineeringManagementServerException.notFound(
                Helper.createDynamicCode(ErrorId.NOT_FOUND_DYNAMIC, SUB_MODULE));
        }
        return activeRequired ? subModuleRepository.findByIdAndIsActiveTrue(id) : subModuleRepository.findById(id);
    }
}
