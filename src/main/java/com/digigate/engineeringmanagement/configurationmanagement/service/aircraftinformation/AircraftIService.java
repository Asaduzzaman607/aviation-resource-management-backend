package com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.aircraftinformation.AircraftDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.AircraftViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.ApuAvailableAircraftViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.adminstration.AircraftInfoViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AircraftIService extends ISearchService<Aircraft, AircraftDto, IdQuerySearchDto> {
    List<AircraftViewModel> getAllAircraft();
    List<AircraftEffectivityTypeViewModel> getAllAircraftByAcModelId(Long acModelId);
    DailyHrsReportAircraftModel findDailyHrsReportAircraftModelByAircraftById(Long aircraftId, LocalDate date, DailyHrsReportTotalModel total);
    AmlLastPageAndAircraftInfo findAircraftInfo(Long aircraftId);
    UtilizationReportResponse utilizationReportHeader(Long aircraftId, UtilizationReportResponse utilizationReportData);

    AircraftInfoViewModel findAircraftInfoData(Long aircraftId);

    List<Aircraft> findAllActiveAircraftByAircraftModel(Long aircraftModelId);

    List<AircraftDropdownViewModel> getAllActiveAircraft();

    List<ApuAvailableAircraftViewModel> getAllApuAvailableAircraft();

    PageData search(IdQuerySearchDto searchDto, Pageable pageable);

}
