package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.planning.entity.AircraftLocation;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface AircraftLocationIService {
    ExcelDataResponse uploadExcel(MultipartFile file);

    Set<AircraftLocation> findAllActiveAircraftLocation();
}
