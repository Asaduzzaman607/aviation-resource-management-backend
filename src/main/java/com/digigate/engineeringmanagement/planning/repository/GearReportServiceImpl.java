package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.constant.LifeLimitUnit;
import com.digigate.engineeringmanagement.planning.constant.ModelType;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.service.LandingGearService;
import com.digigate.engineeringmanagement.planning.util.PlanningUtil;
import com.digigate.engineeringmanagement.planning.service.impl.AircraftBuildService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class GearReportServiceImpl implements LandingGearService {

    private final AircraftBuildRepository aircraftBuildRepository;
    private final AircraftService aircraftService;

    private final AircraftBuildService aircraftBuildService;

    public GearReportServiceImpl(AircraftBuildRepository aircraftBuildRepository, AircraftService aircraftService,
                                 AircraftBuildService aircraftBuildService) {
        this.aircraftBuildRepository = aircraftBuildRepository;
        this.aircraftService = aircraftService;
        this.aircraftBuildService = aircraftBuildService;
    }

    public LandingGearReportViewModel getMlgGearReport(Long partId, Long serialId, Long aircraftId, LocalDate date) {

        Aircraft aircraft = aircraftService.findById(aircraftId);

        if (Objects.nonNull(date)) {
            aircraft = aircraftBuildService.getAircraftInfoByAircraftId(aircraftId, date);
        }

        List<LandingGearViewModel> mlgGearList = new ArrayList<>();
        List<LandingGearViewModel> ownBuildGearList = aircraftBuildRepository.getMlgOwnBuilds(aircraftId, partId, serialId);
        LandingGearViewModel ownBuildGear = new LandingGearViewModel();
        if (CollectionUtils.isNotEmpty(ownBuildGearList)) {
            mlgGearList.addAll(ownBuildGearList);
            ownBuildGear = ownBuildGearList.get(0);
        }

        mlgGearList.addAll(aircraftBuildRepository.getAllMlgBuild(aircraftId, partId, serialId));

        if (CollectionUtils.isNotEmpty(mlgGearList)) {
            prepareGearReport(mlgGearList, aircraft, date);
        }

        return LandingGearReportViewModel.builder()
                .landingGearViewModelList(mlgGearList)
                .asOnDate(aircraft.getUpdatedAt())
                .domDate(Objects.nonNull(aircraft.getManufactureDate()) ? aircraft.getManufactureDate() : null)
                .averageCycIrDay(aircraft.getDailyAverageCycle())
                .averageUtilizationHrsOrDay(aircraft.getDailyAverageHours())
                .tat(aircraft.getAirFrameTotalTime())
                .tac(aircraft.getAirframeTotalCycle())
                .tsn(Objects.nonNull(ownBuildGear) ? ownBuildGear.getCurrentTsn() : null)
                .csn(Objects.nonNull(ownBuildGear) ? ownBuildGear.getCurrentCsn() : null)
                .tso(Objects.nonNull(ownBuildGear) ? ownBuildGear.getCurrentTso() : null)
                .cso(Objects.nonNull(ownBuildGear) ? ownBuildGear.getCurrentCso() : null)
                .build();
    }

    private void prepareGearReport(List<LandingGearViewModel> mlgGearList, Aircraft aircraft, LocalDate date) {

        mlgGearList.forEach(mlg -> {

            if (Objects.nonNull(mlg.getInstallationTsn())) {
                mlg.setCurrentTsn(DateUtil.addTimes(
                        DateUtil.subtractTimes(aircraft.getAirFrameTotalTime(), mlg.getAircraftInHour())
                        , mlg.getInstallationTsn()));
            }

            if (Objects.nonNull(mlg.getInstallationTso())) {
                mlg.setCurrentTso(DateUtil.addTimes(
                        DateUtil.subtractTimes(aircraft.getAirFrameTotalTime(), mlg.getAircraftInHour()),
                        mlg.getInstallationTso()));
            }


            if (Objects.nonNull(mlg.getInstallationCsn())) {
                mlg.setCurrentCsn(aircraft.getAirframeTotalCycle() - mlg.getAircraftInCycle() + mlg.getInstallationCsn());
                if (Objects.nonNull(mlg.getLifeLimit())) {
                    if (mlg.getLifeLimitUnit().equals(LifeLimitUnit.FC)) {
                        mlg.setRemainingDiscard(mlg.getLifeLimit().doubleValue() - mlg.getCurrentCsn());
                    } else if (mlg.getLifeLimitUnit().equals(LifeLimitUnit.FH)) {
                        mlg.setRemainingDiscard(DateUtil.subtractTimes(mlg.getLifeLimit().doubleValue(), mlg.getCurrentTsn()));
                    } else {
                        mlg.setRemainingDiscard(null);
                    }
                }
            }

            if (Objects.nonNull(mlg.getInstallationCso())) {
                mlg.setCurrentCso(aircraft.getAirframeTotalCycle() - mlg.getAircraftInCycle() + mlg.getInstallationCso());
            }

            if (Objects.nonNull(mlg.getOhDueDate())) {
                mlg.setRemainingOhDay(Objects.nonNull(date) ? ChronoUnit.DAYS.between(date, mlg.getOhDueDate())
                        : ChronoUnit.DAYS.between(DateUtil.getCurrentUTCDate(), mlg.getOhDueDate()));
            }

            if (Objects.nonNull(mlg.getCurrentCso()) && Objects.nonNull(mlg.getOhDueCycle())) {
                mlg.setRemainingOhCycle(mlg.getOhDueCycle() - mlg.getCurrentCso());
            }

            LifeLimitUnit unit = mlg.getLifeLimitUnit();
            if (Objects.nonNull(unit)) {
                if (unit.equals(LifeLimitUnit.FC)) {
                    int usedCycle = aircraft.getAirframeTotalCycle() - mlg.getAircraftInCycle();
                    if (Objects.nonNull(mlg.getLifeLimit())) {
                        Integer cycleLeft = mlg.getLifeLimit().intValue() - usedCycle;
                        long dayLeft = (cycleLeft / aircraft.getDailyAverageCycle());
                        LocalDate estimatedDateOfLifeLimit = Objects.nonNull(date) ? date.plusDays(dayLeft)
                                : DateUtil.getCurrentUTCDate().plusDays(dayLeft);
                        if (estimatedDateOfLifeLimit.isBefore(mlg.getEstimatedDueDate())) {
                            mlg.setEstimatedDueDate(estimatedDateOfLifeLimit);
                        }
                    }
                } else if (unit.equals(LifeLimitUnit.FH)) {
                    Double usedHour = DateUtil.subtractTimes(aircraft.getAirFrameTotalTime(), mlg.getAircraftInHour());
                    if (Objects.nonNull(mlg.getLifeLimit())) {
                        Double hourLeft = mlg.getLifeLimit().doubleValue() - usedHour;
                        double dayLeft = (hourLeft / aircraft.getDailyAverageHours());
                        LocalDate estimatedDateOfLifeLimit = Objects.nonNull(date) ? date.plusDays((int) dayLeft)
                                : DateUtil.getCurrentUTCDate().plusDays((int) dayLeft);
                        if (estimatedDateOfLifeLimit.isBefore(mlg.getEstimatedDueDate())) {
                            mlg.setEstimatedDueDate(estimatedDateOfLifeLimit);
                        }
                    }
                } else if (unit.equals(LifeLimitUnit.DY)) {
                    LocalDate estimatedDateOfLifeLimit = mlg.getInstallationDate().plusDays((mlg.getLifeLimit()));
                    if (estimatedDateOfLifeLimit.isBefore(mlg.getEstimatedDueDate())) {
                        mlg.setEstimatedDueDate(estimatedDateOfLifeLimit);
                    }
                }
            }
        });

    }

    @Override
    public List<MlgPartSerialViewModel> getMlgPartSerial(Long aircraftId) {
        return aircraftBuildRepository.findByModelTypeAndAircraft(aircraftId, ModelType.MLG);
    }

    @Override
    public LandingGearReportViewModel getNlgGearReport(Long aircraftId, LocalDate date) {
        Aircraft aircraft = aircraftService.findById(aircraftId);

        if (Objects.nonNull(date)) {
            aircraft = aircraftBuildService.getAircraftInfoByAircraftId(aircraftId, date);
        }

        List<LandingGearViewModel> nlgGearList = new ArrayList<>(aircraftBuildRepository.getAllNgReport(
                aircraftId, ModelType.getNlgModelTypes()));

        Optional<LandingGearViewModel> getTopNlgGear = nlgGearList.stream()
                .filter(nlgGear -> nlgGear.getModelType() == ModelType.NLG).findFirst();

        prepareGearReport(nlgGearList, aircraft, date);

        return LandingGearReportViewModel.builder()
                .landingGearViewModelList(nlgGearList)
                .domDate(aircraft.getManufactureDate())
                .asOnDate(aircraft.getUpdatedAt())
                .averageCycIrDay(aircraft.getDailyAverageCycle())
                .averageUtilizationHrsOrDay(aircraft.getDailyAverageHours())
                .tat(aircraft.getAirFrameTotalTime())
                .tac(aircraft.getAirframeTotalCycle())
                .tsn(getTopNlgGear.map(LandingGearViewModel::getCurrentTsn).orElse(null))
                .csn(getTopNlgGear.map(LandingGearViewModel::getCurrentCsn).orElse(null))
                .tso(getTopNlgGear.map(LandingGearViewModel::getCurrentTso).orElse(null))
                .cso(getTopNlgGear.map(LandingGearViewModel::getCurrentCso).orElse(null))
                .build();
    }

    @Override
    public RemovedLandingGearReportViewModel getRemovedNlgGearReport(Long aircraftId) {
        Aircraft aircraft = aircraftService.findById(aircraftId);
        List<RemovedLandingGearViewModel> nlgGearList = new ArrayList<>(aircraftBuildRepository.getRemovedNlgReport(
                aircraftId, ModelType.getNlgModelTypes()));
        if (!nlgGearList.isEmpty() && Objects.nonNull(nlgGearList.get(0))) {
            aircraft = aircraftBuildService.getAircraftInfoByAircraftId(aircraftId, nlgGearList.get(0).getOutDate());
        }

        Optional<RemovedLandingGearViewModel> getTopNlgGear = nlgGearList.stream()
                .filter(nlgGear -> nlgGear.getModelType() == ModelType.NLG).findFirst();

        prepareRemovedGearReport(nlgGearList, aircraft);

        return RemovedLandingGearReportViewModel.builder()
                .removedLandingGearViewModelList(nlgGearList)
                .domDate(aircraft.getManufactureDate())
                .asOnDate(aircraft.getUpdatedAt())
                .averageCycIrDay(aircraft.getDailyAverageCycle())
                .averageUtilizationHrsOrDay(aircraft.getDailyAverageHours())
                .tat(aircraft.getAirFrameTotalTime())
                .tac(aircraft.getAirframeTotalCycle())
                .tsn(getTopNlgGear.map(RemovedLandingGearViewModel::getCurrentTsn).orElse(null))
                .csn(getTopNlgGear.map(RemovedLandingGearViewModel::getCurrentCsn).orElse(null))
                .tso(getTopNlgGear.map(RemovedLandingGearViewModel::getCurrentTso).orElse(null))
                .cso(getTopNlgGear.map(RemovedLandingGearViewModel::getCurrentCso).orElse(null))
                .build();
    }

    @Override
    public List<MlgPartSerialViewModel> getRemovedMlgPartSerial(Long aircraftId) {
        return aircraftBuildRepository.findByRemovedModelTypeAndAircraft(aircraftId, ModelType.MLG);
    }

    @Override
    public RemovedLandingGearReportViewModel getRemovedMlgGearReport(Long partId, Long serialId, Long aircraftId) {
        Aircraft aircraft = aircraftService.findById(aircraftId);
        List<RemovedLandingGearViewModel> mlgGearList = new ArrayList<>();
        List<RemovedLandingGearViewModel> ownBuildGearList = aircraftBuildRepository
                .getRemovedMlgOwnBuilds(aircraftId, partId, serialId);
        RemovedLandingGearViewModel ownBuildGear = new RemovedLandingGearViewModel();
        if (CollectionUtils.isNotEmpty(ownBuildGearList)) {
            mlgGearList.addAll(ownBuildGearList);
            ownBuildGear = ownBuildGearList.get(0);
            aircraft = aircraftBuildService.getAircraftInfoByAircraftId(aircraftId,ownBuildGear.getOutDate());
        }

        mlgGearList.addAll(aircraftBuildRepository.getAllRemovedMlgBuild(aircraftId, partId, serialId));

        if (CollectionUtils.isNotEmpty(mlgGearList)) {
            prepareRemovedGearReport(mlgGearList, aircraft);
        }

        return RemovedLandingGearReportViewModel.builder()
                .removedLandingGearViewModelList(mlgGearList)
                .asOnDate(aircraft.getUpdatedAt())
                .domDate(Objects.nonNull(aircraft.getManufactureDate()) ? aircraft.getManufactureDate() : null)
                .averageCycIrDay(aircraft.getDailyAverageCycle())
                .averageUtilizationHrsOrDay(aircraft.getDailyAverageHours())
                .tat(aircraft.getAirFrameTotalTime())
                .tac(aircraft.getAirframeTotalCycle())
                .tsn(Objects.nonNull(ownBuildGear) ? ownBuildGear.getCurrentTsn() : null)
                .csn(Objects.nonNull(ownBuildGear) ? ownBuildGear.getCurrentCsn() : null)
                .tso(Objects.nonNull(ownBuildGear) ? ownBuildGear.getCurrentTso() : null)
                .cso(Objects.nonNull(ownBuildGear) ? ownBuildGear.getCurrentCso() : null)
                .build();
    }

    private void prepareRemovedGearReport(List<RemovedLandingGearViewModel> mlgGearList, Aircraft aircraft) {

        mlgGearList.forEach(mlg -> {

            Double usedHour = PlanningUtil.calculateUsedHours(mlg.getAircraftOutHour(), mlg.getAircraftInHour());
            Integer usedCycle = PlanningUtil.calculateUsedCycle(mlg.getAircraftOutCycle(), mlg.getAircraftInCycle());

            if (Objects.nonNull(mlg.getInstallationTsn())) {
                mlg.setCurrentTsn(DateUtil.addTimes(usedHour, mlg.getInstallationTsn()));
            }

            if (Objects.nonNull(mlg.getInstallationTso())) {
                mlg.setCurrentTso(DateUtil.addTimes(usedHour, mlg.getInstallationTso()));
            }


            if (Objects.nonNull(mlg.getInstallationCsn())) {
                mlg.setCurrentCsn(usedCycle + mlg.getInstallationCsn());
                if (Objects.nonNull(mlg.getLifeLimit())) {
                    if (mlg.getLifeLimitUnit().equals(LifeLimitUnit.FC)) {
                        mlg.setRemainingDiscard(mlg.getLifeLimit().doubleValue() - mlg.getCurrentCsn());
                    } else if (mlg.getLifeLimitUnit().equals(LifeLimitUnit.FH)) {
                        mlg.setRemainingDiscard(DateUtil.subtractTimes(mlg.getLifeLimit().doubleValue(), mlg.getCurrentTsn()));
                    } else {
                        mlg.setRemainingDiscard(null);
                    }
                }
            }

            if (Objects.nonNull(mlg.getInstallationCso())) {
                mlg.setCurrentCso(usedCycle + mlg.getInstallationCso());
            }

            if (Objects.nonNull(mlg.getOhDueDate())) {
                mlg.setRemainingOhDay(ChronoUnit.DAYS.between(mlg.getOutDate(), mlg.getOhDueDate()));
            }

            if (Objects.nonNull(mlg.getCurrentCso()) && Objects.nonNull(mlg.getOhDueCycle())) {
                mlg.setRemainingOhCycle(mlg.getOhDueCycle() - mlg.getCurrentCso());
            }

            LifeLimitUnit unit = mlg.getLifeLimitUnit();
            if (Objects.nonNull(unit)) {
                if (unit.equals(LifeLimitUnit.FC)) {
                    if (Objects.nonNull(mlg.getLifeLimit())) {
                        Integer cycleLeft = mlg.getLifeLimit().intValue() - usedCycle;
                        long dayLeft = (cycleLeft / aircraft.getDailyAverageCycle());
                        LocalDate estimatedDateOfLifeLimit = mlg.getOutDate().plusDays(dayLeft);
                        if (estimatedDateOfLifeLimit.isBefore(mlg.getEstimatedDueDate())) {
                            mlg.setEstimatedDueDate(estimatedDateOfLifeLimit);
                        }
                    }
                } else if (unit.equals(LifeLimitUnit.FH)) {
                    if (Objects.nonNull(mlg.getLifeLimit())) {
                        Double hourLeft = mlg.getLifeLimit().doubleValue() - usedHour;
                        double dayLeft = (hourLeft / aircraft.getDailyAverageHours());
                        LocalDate estimatedDateOfLifeLimit = mlg.getOutDate().plusDays((int) dayLeft);
                        if (estimatedDateOfLifeLimit.isBefore(mlg.getEstimatedDueDate())) {
                            mlg.setEstimatedDueDate(estimatedDateOfLifeLimit);
                        }
                    }
                } else if (unit.equals(LifeLimitUnit.DY)) {
                    LocalDate estimatedDateOfLifeLimit = mlg.getInstallationDate().plusDays((mlg.getLifeLimit()));
                    if (estimatedDateOfLifeLimit.isBefore(mlg.getEstimatedDueDate())) {
                        mlg.setEstimatedDueDate(estimatedDateOfLifeLimit);
                    }
                }
            }
        });

    }
}
