package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.ConfigSubmoduleItemResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigSubmoduleItem;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.ConfigMenuSearchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface ISubModuleItemService {
    void updateActiveStatus(Long id, Boolean isActive);

    boolean isPossibleInActiveSubmodule(Long submoduleId);

    List<ConfigSubmoduleItem> getAllSubModuleItemsByIdIn(Set<Long> ids);

    ConfigSubmoduleItem findById(Long id);

    List<ConfigSubmoduleItem> saveItemList(List<ConfigSubmoduleItem> entityList);

    PageData search(ConfigMenuSearchDto searchDto, Pageable pageable);

    ConfigSubmoduleItemResponseDto getSingle(Long id);
}
