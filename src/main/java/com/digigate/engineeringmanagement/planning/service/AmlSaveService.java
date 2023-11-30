package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.entity.AircraftMaintenanceLog;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftMaintenanceLogDto;
import org.springframework.transaction.annotation.Transactional;

public interface AmlSaveService {

    AircraftMaintenanceLog createAml(AircraftMaintenanceLogDto amlDto);


    AircraftMaintenanceLog updateAml(AircraftMaintenanceLogDto amlDto, Long id);

    void deleteAtlInfo(Long aircraftId);
}
