package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.payload.response.LandingGearReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.LandingGearViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.MlgPartSerialViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.RemovedLandingGearReportViewModel;

import java.time.LocalDate;
import java.util.List;

public interface LandingGearService {

    LandingGearReportViewModel getMlgGearReport(Long partId, Long serialId, Long aircraftId, LocalDate date);

    List<MlgPartSerialViewModel> getMlgPartSerial(Long aircraftId);

    LandingGearReportViewModel getNlgGearReport(Long aircraftId, LocalDate date);

    RemovedLandingGearReportViewModel getRemovedNlgGearReport(Long aircraftId);

    List<MlgPartSerialViewModel> getRemovedMlgPartSerial(Long aircraftId);

    RemovedLandingGearReportViewModel getRemovedMlgGearReport(Long partId, Long serialId, Long aircraftId);
}
