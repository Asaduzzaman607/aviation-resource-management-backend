package com.digigate.engineeringmanagement.planning.converter;

import com.digigate.engineeringmanagement.planning.entity.EngineModelType;
import com.digigate.engineeringmanagement.planning.payload.request.EngineModelTypeDto;

import java.util.Objects;

public class EngineModelTypeConverter {
    public static EngineModelType dtoToEntity(EngineModelTypeDto dto, EngineModelType type) {
        if (Objects.nonNull(dto.getId())) {
            type.setId(dto.getId());
        }

        type.setIsActive(dto.getIsActive());
        type.setName(dto.getName());
        type.setDescription(dto.getDescription());
        return type;
    }
}
