package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.common.service.impl.RoleServiceImpl;
import com.digigate.engineeringmanagement.planning.entity.AircraftMaintenanceLog;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftMaintenanceLogDto;
import com.digigate.engineeringmanagement.planning.payload.request.OilRecordSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.AmlDetailsResponseDto;
import com.digigate.engineeringmanagement.planning.repository.AmlDetailsService;
import com.digigate.engineeringmanagement.planning.service.AmlDefectRectificationService;
import com.digigate.engineeringmanagement.planning.service.AmlFlightDataIService;
import com.digigate.engineeringmanagement.planning.service.AmlOilRecordIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AmlDetailsServiceImpl implements AmlDetailsService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final IService<AircraftMaintenanceLog, AircraftMaintenanceLogDto> aircraftMaintenanceLogService;
    private final AmlDefectRectificationService defectRectificationService;

    private final AmlFlightDataIService flightDataService;
    private final AmlOilRecordIService oilRecordIService;


    /**
     * Autowired constructor
     *
     * @param aircraftMaintenanceLogService {@link IService<AircraftMaintenanceLog, AircraftMaintenanceLogDto>}
     * @param defectRectificationService    {@link AmlDefectRectificationService}
     * @param flightDataService             {@link AmlFlightDataIService}
     * @param oilRecordIService             {@link AmlOilRecordIService}
     */


    public AmlDetailsServiceImpl(
            IService<AircraftMaintenanceLog, AircraftMaintenanceLogDto> aircraftMaintenanceLogService,
            AmlDefectRectificationService defectRectificationService,
            AmlFlightDataIService flightDataService,
            AmlOilRecordIService oilRecordIService) {
        this.aircraftMaintenanceLogService = aircraftMaintenanceLogService;
        this.defectRectificationService = defectRectificationService;
        this.flightDataService = flightDataService;
        this.oilRecordIService = oilRecordIService;
    }


    /**
     * responsible for get all model of aml
     *
     * @param id {@link Long}
     * @return AmlDetailsResponseDto as view model
     */
    @Override
    public AmlDetailsResponseDto findAmlDetailsResponseById(Long id) {
        AmlDetailsResponseDto amlDetailsResponseDto = new AmlDetailsResponseDto();
        amlDetailsResponseDto.setAmlResponseData(aircraftMaintenanceLogService.getSingle(id));
        amlDetailsResponseDto.setDefectRectificationResponseDto(defectRectificationService.getDefectRectificationsByAmlId(id));
        amlDetailsResponseDto.setFlightResponseDto(flightDataService.findByAmlId(id));
        try {
            amlDetailsResponseDto.setOilRecordData(oilRecordIService.getOilRecordByAmlId(new OilRecordSearchDto(id, true)));
        } catch (Exception e) {
            LOGGER.error("Data not found for Oil Record");
        }
        return amlDetailsResponseDto;
    }
}
