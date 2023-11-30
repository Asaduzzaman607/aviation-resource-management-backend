package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.planning.entity.Position;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface PositionIService {
    ExcelDataResponse uploadExcel(MultipartFile file);

    Set<Position> findAllActivePosition();

    Set<String> getAllNames();
}
