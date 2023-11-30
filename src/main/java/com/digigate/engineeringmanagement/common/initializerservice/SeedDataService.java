package com.digigate.engineeringmanagement.common.initializerservice;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.entity.AccessRight;
import com.digigate.engineeringmanagement.common.entity.Action;
import com.digigate.engineeringmanagement.common.loader.AccessRightJsonObject;
import com.digigate.engineeringmanagement.common.service.AccessRightService;
import com.digigate.engineeringmanagement.configurationmanagement.constant.ActionEnum;
import com.digigate.engineeringmanagement.configurationmanagement.constant.ModuleEnum;
import com.digigate.engineeringmanagement.configurationmanagement.constant.SubModuleEnum;
import com.digigate.engineeringmanagement.configurationmanagement.constant.SubModuleItemEnum;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigModule;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigSubModule;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigSubmoduleItem;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SeedDataService {

    private final IModuleService moduleService;
    private final ISubModuleService subModuleService;
    private final ISubModuleItemService configSubmoduleItemService;
    private final ActionService actionService;
    private final AccessRightService accessRightService;
    private final ObjectMapper objectMapper;
    private static final Integer ORDER_VALUE = 1;

    public SeedDataService(IModuleService moduleService,
                           ISubModuleService subModuleService,
                           ISubModuleItemService configSubmoduleItemService,
                           ActionService actionService,
                           @Lazy AccessRightService accessRightService,
                           ObjectMapper objectMapper
    ) {
        this.moduleService = moduleService;
        this.subModuleService = subModuleService;
        this.configSubmoduleItemService = configSubmoduleItemService;
        this.actionService = actionService;
        this.accessRightService = accessRightService;
        this.objectMapper = objectMapper;
    }

    @EventListener
    public void postConstruct(ApplicationStartedEvent event) {
        moduleService.saveItemList(getConfigModuleList());
        subModuleService.saveItemList(getConfigSubModuleList());
        configSubmoduleItemService.saveItemList(getConfigSubModuleItemList());
        actionService.saveAll(getActions());
        accessRightService.saveAccessRightList(getAccessRights());
    }

    private List<ConfigModule> getConfigModuleList() {
        return Arrays.stream(ModuleEnum.values())
            .map(this::buildModule).collect(Collectors.toList());
    }

    private List<ConfigSubModule> getConfigSubModuleList() {
        return Arrays.stream(SubModuleEnum.values())
            .map(this::buildSubModule).collect(Collectors.toList());
    }

    private List<ConfigSubmoduleItem> getConfigSubModuleItemList() {
        return Arrays.stream(SubModuleItemEnum.values())
            .map(this::buildSubModuleItem).collect(Collectors.toList());
    }

    private List<AccessRight> getAccessRights() {
        try {
            List<AccessRightJsonObject> accessRightJsonObjects = objectMapper
                .readerForListOf(AccessRightJsonObject.class)
                .readValue(new ClassPathResource(ApplicationConstant.ACCESS_RIGHTS_FILE_PATH).getFile());

            return convertJsonListToAccessRights(accessRightJsonObjects);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    private List<AccessRight> convertJsonListToAccessRights(List<AccessRightJsonObject> accessRightJsonObjects) {
        return accessRightJsonObjects.stream()
            .map(this::convertToAccessRight)
            .collect(Collectors.toList());
    }

    private AccessRight convertToAccessRight(AccessRightJsonObject jsonObject) {
        AccessRight accessRight = new AccessRight();
        accessRight.setId(jsonObject.getId());
        accessRight.setAction(Action.withId(jsonObject.getActionId()));
        accessRight.setConfigSubmoduleItem(ConfigSubmoduleItem.withId(jsonObject.getSubmoduleItemId().longValue()));
        accessRight.setRoleSet(new HashSet<>());
        return accessRight;
    }

    private List<Action> getActions() {
        return Arrays.stream(ActionEnum.values())
            .map(ActionEnum::toEntity)
            .collect(Collectors.toList());
    }

    private ConfigModule buildModule(ModuleEnum moduleEnum) {
        ConfigModule module = new ConfigModule();
        module.setModuleName(moduleEnum.name());
        module.setOrder(moduleEnum.ordinal() + 1);
        module.setId(moduleEnum.getConfigModuleId());
        return module;
    }

    private ConfigSubModule buildSubModule(SubModuleEnum subModuleEnum) {
        ConfigSubModule configSubModule = new ConfigSubModule();
        configSubModule.setId(subModuleEnum.getSubModuleId());
        configSubModule.setSubmoduleName(subModuleEnum.name());
        configSubModule.setOrder(subModuleEnum.ordinal() + 1);
        configSubModule.setModule(ConfigModule.withId(subModuleEnum.getModule().getConfigModuleId()));
        return configSubModule;
    }


    private ConfigSubmoduleItem buildSubModuleItem(SubModuleItemEnum subModuleItemEnum) {
        ConfigSubmoduleItem configSubmoduleItem = new ConfigSubmoduleItem();
        configSubmoduleItem.setId(subModuleItemEnum.getSubModuleItemId());
        configSubmoduleItem.setItemName(subModuleItemEnum.name());
        configSubmoduleItem.setOrder(subModuleItemEnum.ordinal() + ORDER_VALUE);
        configSubmoduleItem.setSubModule(
            ConfigSubModule.withId(subModuleItemEnum.getSubModule().getSubModuleId()));
        return configSubmoduleItem;
    }
}
