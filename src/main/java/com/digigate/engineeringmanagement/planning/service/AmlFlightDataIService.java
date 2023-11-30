package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.AmlFlightData;
import com.digigate.engineeringmanagement.planning.payload.request.AmlFlightDataDto;
import com.digigate.engineeringmanagement.planning.payload.response.AmlFlightDataForOilUpliftReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.AmlFlightViewModel;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface AmlFlightDataIService extends IService<AmlFlightData, AmlFlightDataDto> {

    AmlFlightViewModel findByAmlId(Long amlId);

    List<AmlFlightDataForOilUpliftReportViewModel> getAllFlightDataByAmlIdIn(Set<Long> amlIds);

    List<AmlFlightData> findAllByIds(Set<Long> ids);

    @Transactional
    void migrateFlightDataIntoDailyUtilizationTable(LocalDate updateUpTo);
}
