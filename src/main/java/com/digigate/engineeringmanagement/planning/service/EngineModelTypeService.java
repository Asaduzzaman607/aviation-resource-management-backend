package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.entity.EngineModelType;
import com.digigate.engineeringmanagement.planning.payload.request.EngineModelTypeDto;

import java.util.List;

public interface EngineModelTypeService {
    EngineModelType findById(Integer id);
    Integer save(EngineModelTypeDto engineModelTypeDto);
    Integer update(EngineModelTypeDto engineModelTypeDto);
    Integer toggleStatus(Integer id);
    List<EngineModelType> list();
    EngineModelType findActiveEngineModelType(Integer id);
}
