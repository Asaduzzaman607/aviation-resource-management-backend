package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.planning.entity.Settings;
import com.digigate.engineeringmanagement.planning.payload.request.SettingsDto;
import com.digigate.engineeringmanagement.planning.payload.response.SettingsViewModel;
import com.digigate.engineeringmanagement.planning.repository.SettingsRepository;
import com.digigate.engineeringmanagement.planning.service.SettingsService;
import org.springframework.stereotype.Service;

/**
 * Settings Service
 *
 * @author Asifur Rahman
 */
@Service
public class SettingsServiceImpl extends AbstractService<Settings, SettingsDto> implements SettingsService {


    private final SettingsRepository settingsRepository;

    public SettingsServiceImpl(AbstractRepository<Settings> repository, SettingsRepository settingsRepository) {
        super(repository);
        this.settingsRepository = settingsRepository;
    }

    @Override
    protected SettingsViewModel convertToResponseDto(Settings settings) {
        return SettingsViewModel.builder()
                .id(settings.getId())
                .headerKey(settings.getHeaderKey().toString())
                .headerValue(settings.getHeaderValue())
                .build();
    }

    private Settings mapToEntity(SettingsDto dto, Settings entity) {
        entity.setHeaderKey(dto.getHeaderKey());
        entity.setHeaderValue(dto.getHeaderValue());
        return entity;
    }

    @Override
    protected Settings convertToEntity(SettingsDto settingsDto) {
        return mapToEntity(settingsDto, new Settings());
    }

    @Override
    protected Settings updateEntity(SettingsDto dto, Settings entity) {
        return mapToEntity(dto, entity);
    }


}
