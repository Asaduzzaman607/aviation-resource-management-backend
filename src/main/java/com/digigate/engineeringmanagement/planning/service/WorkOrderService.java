package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.entity.WoTask;
import com.digigate.engineeringmanagement.planning.entity.WorkOrder;
import com.digigate.engineeringmanagement.planning.payload.request.MultipleWorkOrderSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.WoTaskDto;
import com.digigate.engineeringmanagement.planning.payload.request.WorkOrderDto;
import com.digigate.engineeringmanagement.planning.payload.request.WorkOrderSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.WorkOrderRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.util.Precision;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Work Order Service
 *
 * @author ashinisingha
 */
@Service
public class WorkOrderService extends AbstractSearchService<WorkOrder, WorkOrderDto, WorkOrderSearchDto>
        implements WorkOrderIService {

    private final AircraftService aircraftService;
    private final WorkOrderRepository workOrderRepository;

    private static final String AIRCRAFT_ID = "aircraftId";
    private static final String DATE = "date";
    private static final String IS_ACTIVE = "isActive";

    private static final int MAX_DATE_DIFFERENCE = 30;

    /**
     * autowired constructor
     *
     * @param repository          {@link AbstractRepository<WorkOrder>}
     * @param aircraftService     {@link AircraftService}
     * @param workOrderRepository {@link WorkOrderRepository}
     */
    @Autowired
    public WorkOrderService(AbstractRepository<WorkOrder> repository, AircraftService aircraftService,
                            WorkOrderRepository workOrderRepository) {
        super(repository);
        this.aircraftService = aircraftService;
        this.workOrderRepository = workOrderRepository;
    }

    @Override
    protected Specification<WorkOrder> buildSpecification(WorkOrderSearchDto searchDto) {
        CustomSpecification<WorkOrder> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.equalSpecificationAtRoot(searchDto.getAircraftId(), AIRCRAFT_ID)
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getDate(), DATE))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE))
        );
    }

    @Override
    protected WorkOrderViewModel convertToResponseDto(WorkOrder workOrder) {
        WorkOrderViewModel workOrderViewModel = new WorkOrderViewModel();

        Set<WoTask> woTaskSet = workOrder.getWoTaskSet();
        List<WoTaskViewModel> woTaskList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(woTaskSet)) {
            woTaskSet.forEach(woTask -> woTaskList.add(
                    WoTaskViewModel.builder()
                            .id(woTask.getId())
                            .workOrderId(workOrder.getId())
                            .slNo(woTask.getSlNo())
                            .description(woTask.getDescription())
                            .workCardNo(woTask.getWorkCardNo())
                            .complianceDate(woTask.getComplianceDate())
                            .accomplishDate(woTask.getAccomplishDate())
                            .authNo(woTask.getAuthNo())
                            .remarks(woTask.getRemarks())
                            .build()
            ));
        }

        workOrderViewModel.setWoTaskViewModelList(woTaskList.stream().sorted(
                Comparator.comparing(WoTaskViewModel::getSlNo)).collect(Collectors.toList()));

        workOrderViewModel.setId(workOrder.getId());
        workOrderViewModel.setAircraftId(workOrder.getAircraftId());
        if (Objects.isNull(workOrder.getAircraft())) {
            workOrderViewModel.setAircraftName(null);
            workOrderViewModel.setAirframeSerial(null);
        } else {
            workOrderViewModel.setAircraftName(workOrder.getAircraft().getAircraftName());
            workOrderViewModel.setAirframeSerial(workOrder.getAircraft().getAirframeSerial());
        }
        workOrderViewModel.setWorkShopMaint(workOrder.getWorkShopMaint());
        workOrderViewModel.setWoNo(workOrder.getWoNo());
        workOrderViewModel.setDate(workOrder.getDate());
        workOrderViewModel.setTotalAcHours(workOrder.getTotalAcHours());
        workOrderViewModel.setTotalAcLanding(workOrder.getTotalAcLanding());
        workOrderViewModel.setTsnComp(workOrder.getTsnComp());
        workOrderViewModel.setTsoComp(workOrder.getTsoComp());
        workOrderViewModel.setAsOfDate(workOrder.getAsOfDate());
        return workOrderViewModel;
    }

    @Override
    protected WorkOrder convertToEntity(WorkOrderDto workOrderDto) {
        return saveOrUpdate(workOrderDto, new WorkOrder(), false);
    }

    @Override
    protected WorkOrder updateEntity(WorkOrderDto dto, WorkOrder entity) {
        return saveOrUpdate(dto, entity, true);
    }


    private WorkOrder saveOrUpdate(WorkOrderDto workOrderDto, WorkOrder workOrder, Boolean isUpdatable) {

        Aircraft aircraft = aircraftService.findById(workOrderDto.getAircraftId());
        Optional<String> woNoInDb = workOrderRepository.getLastWorkOrderNo(workOrderDto.getAircraftId());
        if (Objects.isNull(workOrder.getId()) && woNoInDb.isPresent()) {
            if (!validateWorkOrderNo(workOrderDto.getWoNo(), woNoInDb.get())) {
                throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_WO_NO);
            }
        }

        workOrder.setAircraft(aircraft);
        workOrder.setWorkShopMaint(workOrderDto.getWorkShopMaint());
        workOrder.setWoNo(workOrderDto.getWoNo());
        workOrder.setDate(workOrderDto.getDate());

        workOrder.setTotalAcHours(Precision.round(workOrderDto.getTotalAcHours(), 2));
        if (!DateUtil.isValidTime(workOrder.getTotalAcHours())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_TOTAL_AC_HRS);
        }
        workOrder.setTotalAcLanding(workOrderDto.getTotalAcLanding());
        workOrder.setTsnComp(workOrderDto.getTsnComp());
        workOrder.setTsoComp(workOrderDto.getTsoComp());
        workOrder.setAsOfDate(workOrderDto.getAsOfDate());
        prepareWoTask(workOrderDto.getWoTaskList(), workOrder, isUpdatable);
        return workOrder;
    }

    private WorkPackageOrderNoData decodeWorkOrderNo(String s) {
        List<String> decode = List.of(s.split("/"));
        int size = decode.size();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu");

        if (size != 4 || !NumberUtils.isDigits(decode.get(size - 1))
                || !isValidYearFormat(decode.get(size - 2), formatter)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_WO_NO);
        }
        return WorkPackageOrderNoData
                .builder()
                .orderNo(Integer.parseInt(decode.get(size - 1)))
                .year(Integer.parseInt(decode.get(size - 2)))
                .build();
    }

    public static boolean isValidYearFormat(String yearString, DateTimeFormatter formatter) {
        try {
            Year.parse(yearString, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    private boolean validateWorkOrderNo(String dtoWoNo, String dbWoNo) {
        WorkPackageOrderNoData newData = decodeWorkOrderNo(dtoWoNo);
        WorkPackageOrderNoData prevData = decodeWorkOrderNo(dbWoNo);
        if (prevData.getYear().equals(newData.getYear())) {
            Integer prevOrderNo = prevData.getOrderNo();
            Integer newOrderNo = newData.getOrderNo();
            return newOrderNo.equals(prevOrderNo + 1);
        } else return prevData.getYear() < (newData.getYear());
    }

    private void prepareWoTask(List<WoTaskDto> woTaskDtoList, WorkOrder workOrder, Boolean isUpdatable) {
        Set<WoTask> woTaskSet = workOrder.getWoTaskSet();
        if (CollectionUtils.isNotEmpty(woTaskDtoList)) {
            if (CollectionUtils.isEmpty(woTaskSet)) {
                woTaskSet = new HashSet<>();
            }

            Map<Long, WoTask> woTaskMap = woTaskSet.stream()
                    .collect(Collectors.toMap(WoTask::getId, woTask -> woTask));
            Set<Long> woTaskIds = new HashSet<>();
            woTaskDtoList.forEach(woTaskDto -> {
                WoTask woTask;
                if (Objects.nonNull(woTaskDto.getWoTaskId()) && woTaskMap.containsKey(woTaskDto.getWoTaskId())) {
                    woTask = woTaskMap.get(woTaskDto.getWoTaskId());
                    woTaskIds.add(woTaskDto.getWoTaskId());
                } else {
                    woTask = new WoTask();
                }
                woTask.setWorkOrder(workOrder);
                woTask.setSlNo(woTaskDto.getSlNo());
                woTask.setDescription(woTaskDto.getDescription());
                woTask.setWorkCardNo(woTaskDto.getWorkCardNo());
                woTask.setComplianceDate(woTaskDto.getComplianceDate());
                woTask.setAccomplishDate(woTaskDto.getAccomplishDate());
                woTask.setAuthNo(woTaskDto.getAuthNo());
                woTask.setRemarks(woTaskDto.getRemarks());
                workOrder.addWoTask(woTask);
            });
            if (isUpdatable && CollectionUtils.isNotEmpty(workOrder.getWoTaskSet())) {
                workOrder.getWoTaskSet().removeIf(woTask -> Objects.nonNull(woTask.getId())
                        && !woTaskIds.contains(woTask.getId()));
            }

        } else {
            if (CollectionUtils.isNotEmpty(woTaskSet)) {
                woTaskSet.clear();
            }
        }
    }

    /**
     * This method is responsible for getting aircraft data by aircraft id
     *
     * @param aircraftId {@link Long}
     * @return {@link WorkOrderAirCraftViewModel}
     */
    @Override
    public WorkOrderAirCraftViewModel getAircraftData(Long aircraftId) {
        Aircraft aircraft = aircraftService.findById(aircraftId);
        Optional<String> woNoInDb = workOrderRepository.getLastWorkOrderNo(aircraftId);
        if (woNoInDb.isPresent()) {
            StringBuilder sb = new StringBuilder(woNoInDb.get());
            int len = sb.length() - 1;
            char lastChar = sb.charAt(len);
            lastChar++;
            sb.replace(len, len + 1, Character.toString(lastChar));
            return new WorkOrderAirCraftViewModel(aircraft.getAirFrameTotalTime(), sb, aircraft.getAirframeTotalCycle(),
                    aircraft.getAirframeSerial(), aircraft.getUpdatedAt());
        } else {
            return new WorkOrderAirCraftViewModel(aircraft.getAirFrameTotalTime(), new StringBuilder("1"),
                    aircraft.getAirframeTotalCycle(), aircraft.getAirframeSerial(), aircraft.getUpdatedAt());
        }

    }

    /**
     * This method is responsible for getting work order data by aircraft id
     *
     * @param aircraftId {@link Long}
     * @return workOrderAcCheckIndexViewModel {@link WorkOrderAcCheckIndexViewModel}
     */
    @Override
    public List<WorkOrderAcCheckIndexViewModel> getWorkOrderDataByAircraftId(Long aircraftId) {
        return workOrderRepository.findAllWorkOrderDataByAircraftId(aircraftId);
    }

    @Override
    public List<WorkOrderViewModel> getMultipleReport(MultipleWorkOrderSearchDto multipleWorkOrderSearchDto) {
        validateMonthRange(multipleWorkOrderSearchDto.getFromDate(), multipleWorkOrderSearchDto.getToDate());
        List<WorkOrder> workOrder = workOrderRepository.findAllWorkOrderBetweenDateRange(multipleWorkOrderSearchDto.getAircraftId()
                ,multipleWorkOrderSearchDto.getIsActive(),multipleWorkOrderSearchDto.getFromDate()
                ,multipleWorkOrderSearchDto.getToDate());
        List<WorkOrderViewModel> workOrderViewModelList = new ArrayList<>();
        for (WorkOrder order : workOrder) {
            WorkOrderViewModel workOrderViewModel = convertToResponseDto(order);
            workOrderViewModelList.add(workOrderViewModel);
        }
        return workOrderViewModelList;
    }

    private void validateMonthRange(LocalDate fromDate, LocalDate toDate) {
        if (ChronoUnit.DAYS.between(fromDate, toDate) > MAX_DATE_DIFFERENCE) {
            throw new EngineeringManagementServerException(
                    ErrorId.ONE_MONTH_DATE_RANGE_LIMIT_ERROR, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }
}
