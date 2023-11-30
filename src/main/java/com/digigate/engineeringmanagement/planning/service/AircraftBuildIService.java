package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.dto.AcBuildPartReturnDto;
import com.digigate.engineeringmanagement.planning.entity.AircraftBuild;
import com.digigate.engineeringmanagement.planning.payload.request.*;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AircraftBuildIService extends ISearchService<AircraftBuild,
        AircraftBuildPayload, AircraftBuildSearchPayload> {
    List<AircraftBuild> findByAircraftId(Long aircraftId);
    List<AircraftBuild> saveAll(List<AircraftBuild> aircraftBuildList);
    ExcelDataResponse uploadExcel(MultipartFile file, Long aircraftId);
    AircraftBuildPartSerialSearchViewModel searchByPartIdAndSerial(AircraftBuildPartSerialSearchDto
                                                                           aircraftBuildPartSerialSearchDto);
    PropellerResponseData getPropellerReport(PropellerReportDto propellerReportDto);
    AircraftBuildPartSerialSearchViewModel searchByPartIdAndSerialByStoreInspection(
            AircraftBuildPartSerialSearchDto aircraftBuildPartSerialSearchDto);
    List<PropellerACBuildIdAndPositionViewModel> getPropellerPositionNameByAircraftId(Long aircraftId);
    void makeAcBuildInActive(AircraftBuildInactiveDto dto);
    Page<OCCMViewModel> findOCCMByAircraftId(OCCMSearchDto occmSearchDto, Pageable pageable);

    List<EngineViewModel> findAircraftEnginesByAircraftId(Long aircraftId);

    AcComponentViewModel getComponentHistoryList(Long partNo, Long serialNo);

    List<AircraftBuild> findAllTmmAndRgbByHigherSerialAndPart(Long higherSerialId, Long higherPartId);

    List<AircraftBuild> findAllInactivateTmmAndRgbByHigherSerialAndPart(Long higherSerialId, Long higherPartId);
    List<AircraftBuild> findAllEngineLlpParts(Long serialId, Long partId);

    List<AircraftBuild> findAllInactivateEngineLlpParts(Long serialId, Long partId);
    Set<AcSerialResponse> findAcSerialResponseByPartIdAndModelId(Long partId, Long modelId);

    Set<AcPartResponse> getAcPartResponseByModelId(Long modelId);

    AircraftEngineDetailsViewModel findAircraftEngineDetailsForAdReport(Long serialId, Long partId, Long aircraftId,
                                                                        LocalDate date);

    ApuStatusReportViewModel getApuStatusReport(Long aircraftId);

    Optional<AcBuildPartReturnDto> getAcBuildPartReturn(Long partId, Long serialId);

    List<EngineViewModel> findInactivateAircraftEnginesByAircraftId(Long aircraftId);

    Aircraft getAircraftInfoByAircraftBuildId(Long aircraftBuildId, LocalDate givenDate);

    ApuStatusReportViewModel getApuRemovedStatusReport(Long aircraftId);

    List<AircraftBuildExcelViewModel> getAllBuildAircraft();
}
