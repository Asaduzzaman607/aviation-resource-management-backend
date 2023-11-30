package com.digigate.engineeringmanagement.configurationmanagement.service.administration;

import com.digigate.engineeringmanagement.common.payload.response.ModuleViewModel;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.ConfigModuleResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ConfigModule;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.ConfigMenuSearchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IModuleService {

    void updateActiveStatus(Long id, Boolean isActive);

    List<ModuleViewModel> getAllModule();

    List<ConfigModule> saveItemList(List<ConfigModule> entityList);

    PageData search(ConfigMenuSearchDto searchDto, Pageable pageable);

    ConfigModuleResponseDto getSingle(Long id);

    ConfigModule findByIdUnfiltered(Long moduleId);
}
