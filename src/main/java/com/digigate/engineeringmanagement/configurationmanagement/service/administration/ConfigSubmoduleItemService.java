package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.loader.SubModuleJsonLoader;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration.ConfigSubmoduleItemDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.ConfigSubmoduleItemResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigModule;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigSubModule;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigSubmoduleItem;
import com.digigate.engineeringmanagement.configurationmanagement.repository.administration.ConfigSubmoduleItemRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.ConfigMenuSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.ConfigMenuSearchDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;

@Service
public class ConfigSubmoduleItemService implements ISubModuleItemService {
    private final ConfigSubmoduleItemRepository itemRepository;
    private final ISubModuleService subModuleService;
    protected static final Logger LOGGER = LoggerFactory.getLogger(ConfigSubmoduleItemService.class);

    public ConfigSubmoduleItemService(ConfigSubmoduleItemRepository itemRepository,
                                      ISubModuleService subModuleService) {
        this.itemRepository = itemRepository;
        this.subModuleService = subModuleService;
    }

    public ConfigSubmoduleItem update(ConfigSubmoduleItemDto dto, Long id) {
        ConfigSubmoduleItem configSubmoduleItem = findByIdUnfiltered(id);
        validate(dto, configSubmoduleItem);
        final ConfigSubmoduleItem convertedConfigSubmoduleItem = updateEntity(dto, configSubmoduleItem);
        return saveItem(convertedConfigSubmoduleItem);
    }

    private ConfigSubmoduleItem saveItem(ConfigSubmoduleItem entity) {
        try {
            return itemRepository.save(entity);
        } catch (Exception e) {
            String name = entity.getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", name);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(
                Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    name));
        }
    }

    private ConfigSubmoduleItem findByIdUnfiltered(Long id) {
        return findOptionalById(id, false)
            .orElseThrow(() -> EngineeringManagementServerException.notFound(
                Helper.createDynamicCode(ErrorId.NOT_FOUND_DYNAMIC, SUB_MODULE_ITEM)));
    }

    private Optional<ConfigSubmoduleItem> findOptionalById(Long id, boolean activeRequired) {
        if (ObjectUtils.isEmpty(id)) {
            throw EngineeringManagementServerException.notFound(
                Helper.createDynamicCode(ErrorId.NOT_FOUND_DYNAMIC, SUB_MODULE_ITEM));
        }
        return activeRequired ? itemRepository.findByIdAndIsActiveTrue(id) : itemRepository.findById(id);
    }

    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == Boolean.FALSE && itemRepository.existsByBaseItemAndIsActiveTrue(id)) {
            throw new EngineeringManagementServerException(
                ErrorId.ITEM_HAS_DEPENDENCY_AS_BASE_ITEM,
                HttpStatus.PRECONDITION_FAILED,
                MDC.get(TRACE_ID)
            );
        }
        ConfigSubmoduleItem e = findByIdUnfiltered(id);
        if (Objects.equals(e.getIsActive(), isActive)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }
        e.setIsActive(isActive);
        saveItem(e);
    }

    @Override
    public List<ConfigSubmoduleItem> getAllSubModuleItemsByIdIn(Set<Long> ids) {
        return itemRepository.findAllByIdIn(ids);

    }

    @Override
    public ConfigSubmoduleItem findById(Long id) {
        ConfigSubmoduleItem configSubmoduleItem = new ConfigSubmoduleItem();
        Optional<ConfigSubmoduleItem> submoduleItemOptional = itemRepository.findById(id);
        if (submoduleItemOptional.isPresent()) {
            configSubmoduleItem = submoduleItemOptional.get();
        }
        return configSubmoduleItem;
    }

    @Override
    public List<ConfigSubmoduleItem> saveItemList(List<ConfigSubmoduleItem> entityList) {
        try {
            if (CollectionUtils.isEmpty(entityList)) {
                return entityList;
            }
//            if (itemRepository.findById(entityList.get(FIRST_INDEX).getId()).isPresent()) {
//                return Collections.emptyList();
//            }
            return itemRepository.saveAll(entityList);
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
        Specification<ConfigSubmoduleItem> customSpecification = buildSpecification(searchDto);
        Page<ConfigSubmoduleItem> pagedData = itemRepository.findAll(customSpecification, pageable);
        List<Object> models = pagedData.getContent()
            .stream().map(this::convertToResponseDto).collect(Collectors.toList());
        return PageData.builder()
            .model(models)
            .totalPages(pagedData.getTotalPages())
            .totalElements(pagedData.getTotalElements())
            .currentPage(pageable.getPageNumber() + 1)
            .build();
    }

    @Override
    public ConfigSubmoduleItemResponseDto getSingle(Long id) {
        return convertToResponseDto(findByIdUnfiltered(id));
    }

    protected ConfigSubmoduleItemResponseDto convertToResponseDto(ConfigSubmoduleItem item) {
        ConfigSubModule subModule = item.getSubModule();
        ConfigModule module = subModule.getModule();
        ConfigSubmoduleItem configSubmoduleItem = new ConfigSubmoduleItem();
        if (Objects.nonNull(item.getBaseItem())) {
            configSubmoduleItem = findById(item.getBaseItem());
            item.setIsBase(false);
        }
        return ConfigSubmoduleItemResponseDto.builder()
            .id(item.getId())
            .subModuleId(subModule.getId())
            .subModuleName(subModule.getSubmoduleName())
            .moduleId(module.getId())
            .moduleName(module.getModuleName())
            .itemName(item.getItemName())
            .itemNameHrf(Helper.convertSnakeToHrf(item.getItemName()))
            .urlPath(item.getUrlPath())
            .order(item.getOrder())
            .isBase(item.getIsBase())
            .baseItem(item.getBaseItem())
            .baseItemName(configSubmoduleItem.getItemName())
            .isActive(item.getIsActive())
            .build();
    }

    private ConfigSubmoduleItem updateEntity(ConfigSubmoduleItemDto dto, ConfigSubmoduleItem item) {
        item.setSubModule(subModuleService.findByIdUnfiltered(dto.getSubModuleId()));
        return buildConfigSubmoduleItem(dto, item);
    }

    @Override
    public boolean isPossibleInActiveSubmodule(Long submoduleId) {
        return itemRepository.existsBySubModuleIdAndIsActiveTrue(submoduleId);
    }

    protected Specification<ConfigSubmoduleItem> buildSpecification(ConfigMenuSearchDto searchDto) {
        CustomSpecification<ConfigSubmoduleItem> customSpecification = new CustomSpecification<>();
        Set<Long> workflowSubmoduleItemIds = null;
        if (searchDto.getIsWorkflow() == Boolean.TRUE) {
            Map<String, Long> subModuleApis = SubModuleJsonLoader.SUB_MODULE_APIS;
            workflowSubmoduleItemIds = new HashSet<>(subModuleApis.values());
        }
        return Specification.where(customSpecification.active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD))
            .and(customSpecification.inSpecificationAtRoot(workflowSubmoduleItemIds, ID))
            .and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), CONFIG_SUB_MODULE_ITEM_NAME));
    }

    private ConfigSubmoduleItem buildConfigSubmoduleItem(ConfigSubmoduleItemDto dto, ConfigSubmoduleItem item) {
        item.setItemName(dto.getItemName());
        item.setUrlPath(dto.getUrlPath());
        item.setOrder(dto.getOrder());
        if (BooleanUtils.isNotTrue(dto.getIsBase()) && Objects.isNull(dto.getBaseItem())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.BASE_ITEM_ID_REQUIRED);
        } else {
            item.setIsBase(dto.getIsBase());
            item.setBaseItem(dto.getBaseItem());
        }
        item.setIsActive(Objects.isNull(dto.getIsActive()) ? Boolean.TRUE : dto.getIsActive());
        return item;
    }

    /**
     * This method is responsible for validating case-sensitive submodule item name
     *
     * @param configSubmoduleItemDto {@link ConfigSubmoduleItemDto}
     * @param old                    {@link ConfigSubmoduleItem}
     */
    private void validate(ConfigSubmoduleItemDto configSubmoduleItemDto, ConfigSubmoduleItem old) {
        List<ConfigSubmoduleItem> configSubmoduleItemList =
            itemRepository.findBySubModuleIdAndItemNameIgnoreCase(
                configSubmoduleItemDto.getSubModuleId(),
                configSubmoduleItemDto.getItemName());

        if (CollectionUtils.isNotEmpty(configSubmoduleItemList) && (
            Objects.isNull(old) ||
                configSubmoduleItemList.size() > VALUE_ONE ||
                !configSubmoduleItemList.get(FIRST_INDEX).equals(old))) {
            throw EngineeringManagementServerException.badRequest(
                ErrorId.ITEM_NAME_ALREADY_EXIST);
        }
    }

}

