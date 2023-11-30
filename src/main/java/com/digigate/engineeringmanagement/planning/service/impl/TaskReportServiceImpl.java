package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.constant.HourCalculationType;
import com.digigate.engineeringmanagement.planning.constant.ModelType;
import com.digigate.engineeringmanagement.planning.constant.TaskStatusEnum;
import com.digigate.engineeringmanagement.planning.entity.Ldnd;
import com.digigate.engineeringmanagement.planning.payload.request.*;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.AircraftBuildRepository;
import com.digigate.engineeringmanagement.planning.repository.LdndRepository;
import com.digigate.engineeringmanagement.planning.service.LdndService;
import com.digigate.engineeringmanagement.planning.service.TaskReportService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskReportServiceImpl implements TaskReportService {

    private final LdndRepository ldndRepository;
    private final LdndService ldndService;
    private final AircraftService aircraftService;
    private final AircraftBuildRepository aircraftBuildRepository;

    private final SharedAircraftInformation sharedAircraftInformation;

    @Autowired
    public TaskReportServiceImpl(LdndRepository ldndRepository, LdndService ldndService, AircraftService aircraftService,
                                 AircraftBuildRepository aircraftBuildRepository,
                                 SharedAircraftInformation sharedAircraftInformation) {
        this.ldndRepository = ldndRepository;
        this.ldndService = ldndService;
        this.aircraftService = aircraftService;
        this.aircraftBuildRepository = aircraftBuildRepository;
        this.sharedAircraftInformation = sharedAircraftInformation;
    }


    @Override
    public PageData getLdNdReportData(LdndReportSearchDto searchDto, Pageable pageable) {
        if (Objects.nonNull(searchDto.getFromDate()) && Objects.nonNull(searchDto.getToDate())) {
            DateUtil.isValidateDateRange(searchDto.getFromDate(), searchDto.getToDate());
        }
        Page<TaskReportViewModel> taskReportViewModelPage = ldndRepository.getLdndReport(
                searchDto.getAircraftId(),
                TaskStatusEnum.CLOSED,
                searchDto.getFromDate(),
                searchDto.getToDate(),
                ApplicationConstant.TASK_SOURCE_AMP,
                searchDto.getIntervalCycle(),
                searchDto.getIntervalHour(),
                searchDto.getIntervalDay(),
                searchDto.getThCycle(),
                searchDto.getThHour(),
                searchDto.getThDay(),
                searchDto.getAmpTaskNo(),
                searchDto.getIsPageable() ? pageable : Pageable.unpaged());

        taskReportViewModelPage.getContent().forEach(d -> {
            if (Objects.nonNull(d.getDueDate())) {
                d.setRemainingDay(ChronoUnit.DAYS.between(DateUtil.getCurrentUTCDate(), d.getDueDate()));
            }
        });
        return PageData.builder()
                .model(taskReportViewModelPage.getContent())
                .totalPages(taskReportViewModelPage.getTotalPages())
                .totalElements(taskReportViewModelPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();

    }

    @Override
    public PageData getLdndHardTimeReport(LdndReportSearchDto searchDto, Pageable pageable) {
        if (Objects.nonNull(searchDto.getFromDate()) && Objects.nonNull(searchDto.getToDate())) {
            DateUtil.isValidateDateRange(searchDto.getFromDate(), searchDto.getToDate());
        }
        Page<HardTimeReportViewModel> hardTimeReportViewModels = ldndRepository.getLdndHardTimeReport(
                searchDto.getAircraftId(),
                TaskStatusEnum.CLOSED,
                ModelType.getAllHardTimeModelTypes(),
                searchDto.getFromDate(),
                searchDto.getToDate(),
                ApplicationConstant.TASK_SOURCE_AMP,
                searchDto.getIntervalCycle(),
                searchDto.getIntervalHour(),
                searchDto.getIntervalDay(),
                searchDto.getThCycle(),
                searchDto.getThHour(),
                searchDto.getThDay(),
                searchDto.getModel(),
                searchDto.getPartNo(),
                searchDto.getSerialNumber(),
                searchDto.getIsPageable() ? pageable : Pageable.unpaged());

        hardTimeReportViewModels.getContent().forEach(d -> {
            if (Objects.nonNull(d.getDueDate())) {
                d.setRemainingDay(ChronoUnit.DAYS.between(DateUtil.getCurrentUTCDate(), d.getDueDate()));
            }
        });
        return PageData.builder()
                .model(hardTimeReportViewModels.getContent())
                .totalPages(hardTimeReportViewModels.getTotalPages())
                .totalElements(hardTimeReportViewModels.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();

    }


    @Override
    public List<Long> updateLdndData() {
        List<Ldnd> ldnds = ldndRepository.findAllByIsActiveTrue();

        ldnds.forEach(ldnd -> {
            if (ldnd.getIsApuControl()) {
                ldndService.calculateRemainForApu(ldnd, ldnd.getAircraft());
                ldndService.calculateEstimatedDateForApu(ldnd, ldnd.getAircraft(), null);
            } else {
                ldndService.calculateRemainForFlight(ldnd, ldnd.getAircraft());
                ldndService.calculateEstimatedDateForFlight(ldnd, ldnd.getAircraft(), null);
            }
            ldndRepository.save(ldnd);
        });

        List<Ldnd> updatedList = ldndRepository.saveAll(ldnds);
        return updatedList.stream().map(AbstractDomainBasedEntity::getId).collect(Collectors.toList());
    }

    /**
     * This method is responsible for AD report generation
     *
     * @param adReportSearchDto {@link AdReportSearchDto}
     * @param pageable          {@link Pageable}
     * @return pageData {@link Page<AdReportViewModel> }
     */
    @Override
    public PageData getAdReportData(AdReportSearchDto adReportSearchDto, Pageable pageable) {
        if (Objects.nonNull(adReportSearchDto.getFromDate()) && Objects.nonNull(adReportSearchDto.getToDate())) {
            DateUtil.isValidateDateRange(adReportSearchDto.getFromDate(), adReportSearchDto.getToDate());
        }

        Page<AdReportViewModel> adReportViewModelPage = ldndRepository.getAdReportData(
                adReportSearchDto.getAircraftId(),
                ModelType.getAirframeApplianceAdModelTypes(),
                adReportSearchDto.getFromDate(),
                adReportSearchDto.getToDate(),
                ApplicationConstant.TASK_SOURCE_AD,
                adReportSearchDto.getIntervalCycle(),
                adReportSearchDto.getIntervalHour(),
                adReportSearchDto.getIntervalDay(),
                adReportSearchDto.getThCycle(),
                adReportSearchDto.getThHour(),
                adReportSearchDto.getThDay(),
                adReportSearchDto.getIsPageable() ? pageable : Pageable.unpaged());


        adReportViewModelPage.getContent().forEach(d -> {
            if (Objects.nonNull(d.getNextDueDate())) {
                d.setRemainingDay(ChronoUnit.DAYS.between(DateUtil.getCurrentUTCDate(), d.getNextDueDate()));
            }
            if (Objects.nonNull(d.getStatus())) {
                if (d.getStatus().getTaskStatusType().equals(TaskStatusEnum.OPEN.getTaskStatusType())) {
                    makeLastDoneNull(d);
                } else if (d.getStatus().getTaskStatusType().equals(TaskStatusEnum.CLOSED.getTaskStatusType())) {
                    makeNextDueRemainNull(d);
                }
            } else {
                d.setStatus(d.getTaskStatus());
            }
        });

        return PageData.builder()
                .model(adReportViewModelPage.getContent())
                .totalPages(adReportViewModelPage.getTotalPages())
                .totalElements(adReportViewModelPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }


    @Override
    public PageData getSbReport(AdReportSearchDto adReportSearchDto, Pageable pageable) {
        if (Objects.nonNull(adReportSearchDto.getFromDate()) && Objects.nonNull(adReportSearchDto.getToDate())) {
            DateUtil.isValidateDateRange(adReportSearchDto.getFromDate(), adReportSearchDto.getToDate());
        }
        Page<SbReportViewModel> sbReportViewModels = ldndRepository.getSbReport(
                adReportSearchDto.getAircraftId(),
                adReportSearchDto.getTaskNo(),
                ApplicationConstant.TASK_SOURCE_SB,
                adReportSearchDto.getIsPageable() ? pageable : Pageable.unpaged());

        return PageData.builder()
                .model(sbReportViewModels.getContent())
                .totalPages(sbReportViewModels.getTotalPages())
                .totalElements(sbReportViewModels.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public PageData getStcReport(AdReportSearchDto adReportSearchDto, Pageable pageable) {
        if (Objects.nonNull(adReportSearchDto.getFromDate()) && Objects.nonNull(adReportSearchDto.getToDate())) {
            DateUtil.isValidateDateRange(adReportSearchDto.getFromDate(), adReportSearchDto.getToDate());
        }
        Page<StcAndModViewModel> stcAndModViewModels = ldndRepository.getStcReport(
                adReportSearchDto.getAircraftId(),
                adReportSearchDto.getTaskNo(),
                ApplicationConstant.otherTaskSource,
                adReportSearchDto.getFromDate(),
                adReportSearchDto.getToDate(),
                adReportSearchDto.getIsPageable() ? pageable : Pageable.unpaged());

        stcAndModViewModels.forEach(d->{
            if(d.getStatus().equals(TaskStatusEnum.CLOSED)){
                d.setDueHour(null);
                d.setDueCycle(null);
                d.setDueDate(null);
            }
        });

        return PageData.builder()
                .model(stcAndModViewModels.getContent())
                .totalPages(stcAndModViewModels.getTotalPages())
                .totalElements(stcAndModViewModels.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public PageData getAdEngineReportData(EngineAdReportSearchDto dto, Pageable pageable) {
        Page<AdReportViewModel> adReportViewModel = ldndRepository.getAdEngineReportData(
                dto.getAircraftId(),
                dto.getDate(),
                ModelType.getEngineModelTypes(),
                ApplicationConstant.TASK_SOURCE_AD,
                dto.getIsPageable() ? pageable : Pageable.unpaged());

        Set<AcEnginePartSerialData> serialResponseSet = aircraftBuildRepository.findEnginePartSerials(dto.getSerialId(),
                dto.getPartId());

        Set<AdReportViewModel> finalResponse = new HashSet<>();

        adReportViewModel.forEach(d -> {
            if (Objects.nonNull(d.getNextDueDate())) {
                d.setRemainingDay(ChronoUnit.DAYS.between(Objects.nonNull(dto.getDate()) ? dto.getDate()
                        : DateUtil.getCurrentUTCDate(), d.getNextDueDate()));
            }

            if (Objects.nonNull(d.getNextDueFlyingHour()) && Objects.nonNull(sharedAircraftInformation.getTat())) {
                d.setRemainingFlyingHour(DateUtil.calculateHour(d.getNextDueFlyingHour(),
                        sharedAircraftInformation.getTat(),
                        HourCalculationType.SUBTRACT));
            }

            if (Objects.nonNull(d.getNextDueFlyingCycle()) && Objects.nonNull(sharedAircraftInformation.getTac())) {
                d.setRemainingFlyingCycle(d.getNextDueFlyingCycle() - sharedAircraftInformation.getTac());
            }
            if (Objects.nonNull(d.getId())) {
                if (serialResponseSet.contains(new AcEnginePartSerialData(d.getPartId(), d.getSerialNo()))) {
                    if (Objects.nonNull(d.getStatus())) {
                        if (d.getStatus().getTaskStatusType().equals(TaskStatusEnum.OPEN.getTaskStatusType())) {
                            makeLastDoneNull(d);
                        } else if (d.getStatus().getTaskStatusType().equals(TaskStatusEnum.CLOSED.getTaskStatusType())) {
                            makeNextDueRemainNull(d);
                        }
                    } else {
                        d.setStatus(d.getTaskStatus());
                    }
                    finalResponse.add(d);
                } else {
                    makeAdReportFieldNull(d);
                    finalResponse.add(d);
                }
            } else {
                finalResponse.add(d);
            }
        });
        return PageData.builder()
                .model(Arrays.asList(finalResponse.toArray()))
                .totalPages(adReportViewModel.getTotalPages())
                .totalElements(adReportViewModel.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private void makeAdReportFieldNull(AdReportViewModel data) {
        data.setId(null);
        data.setLastDoneDate(null);
        data.setLastDoneFlyingCycle(null);
        data.setLastDoneFlyingHour(null);
        data.setNextDueDate(null);
        data.setNextDueFlyingHour(null);
        data.setNextDueFlyingCycle(null);
        data.setRemainingDay(null);
        data.setRemainingFlyingCycle(null);
        data.setRemainingFlyingHour(null);
        data.setRemarks(null);
        data.setStatus(null);
    }

    private void makeLastDoneNull(AdReportViewModel data) {
        data.setLastDoneDate(null);
        data.setLastDoneFlyingHour(null);
        data.setLastDoneFlyingCycle(null);
    }

    private void makeNextDueRemainNull(AdReportViewModel data) {
        data.setNextDueDate(null);
        data.setNextDueFlyingHour(null);
        data.setNextDueFlyingCycle(null);
        data.setRemainingDay(null);
        data.setRemainingFlyingHour(null);
        data.setRemainingFlyingCycle(null);
    }

    /**
     * This method is responsible for AD report title generation
     *
     * @param aircraftId {@link Long}
     * @return adReportTitleDataViewModel {@link AdReportTitleDataViewModel}
     */
    @Override
    public AdReportTitleDataViewModel getAdReportTitleData(Long aircraftId) {
        return aircraftService.getAdReportTitleData(aircraftId);
    }

    @Override
    public PageData getTaskStatusReport(TaskStatusReportSearchDto searchDto, Pageable pageable) {
        Page<TaskStatusReport> taskStatusReportPage;
        if (Objects.nonNull(searchDto.getTaskId())) {
            if (Objects.nonNull(searchDto.getAircraftId())) {
                taskStatusReportPage = ldndRepository.getTaskStatusReportByTaskIdAndAircraft(
                        searchDto.getTaskId(), searchDto.getAircraftId(), searchDto.getIsPageable() ? pageable : Pageable.unpaged());
            } else {
                taskStatusReportPage = ldndRepository.getTaskStatusReportByTaskId(
                        searchDto.getTaskId(), searchDto.getIsPageable() ? pageable : Pageable.unpaged());
            }
        } else {
//            for reusing this api using task no
            if (Objects.nonNull(searchDto.getAircraftId())) {
                taskStatusReportPage = ldndRepository.getTaskStatusReportByTaskNoAndAircraft(
                        searchDto.getTaskNo(), searchDto.getAircraftId(), searchDto.getIsPageable() ? pageable : Pageable.unpaged());
            } else {
                taskStatusReportPage = ldndRepository.getTaskStatusReportByTaskNo(
                        searchDto.getTaskNo(), searchDto.getIsPageable() ? pageable : Pageable.unpaged());
            }
        }

        taskStatusReportPage.getContent().forEach(d -> {
            if (Objects.nonNull(d.getNextDueDate())) {
                d.setRemainingDay(ChronoUnit.DAYS.between(DateUtil.getCurrentUTCDate(), d.getNextDueDate()));
            }
        });

        return PageData.builder()
                .model(taskStatusReportPage.getContent())
                .totalPages(taskStatusReportPage.getTotalPages())
                .totalElements(taskStatusReportPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    public PageData searchTaskListByTaskSourceType(TaskListSearchDto searchDto, Pageable pageable) {
        Page<SearchedTasks> searchedTasks = ldndRepository.searchTaskListByTaskSourceType(searchDto.getTaskNo(), pageable);
        return PageData.builder()
                .model(searchedTasks.getContent())
                .totalPages(searchedTasks.getTotalPages())
                .totalElements(searchedTasks.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

}
