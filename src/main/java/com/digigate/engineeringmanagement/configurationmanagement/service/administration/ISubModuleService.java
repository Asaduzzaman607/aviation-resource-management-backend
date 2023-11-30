package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.ConfigSubModuleResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigSubModule;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.ConfigMenuSearchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISubModuleService {
    void updateActiveStatus(Long id, Boolean isActive);

    PageData search(ConfigMenuSearchDto searchDto, Pageable pageable);

    ConfigSubModuleResponseDto getSingle(Long id);

    List<ConfigSubModule> saveItemList(List<ConfigSubModule> configSubModuleList);

    boolean isModuleInActivePossible(Long id);

    ConfigSubModule findByIdUnfiltered(Long subModuleId);
}
