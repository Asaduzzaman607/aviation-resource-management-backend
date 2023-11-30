package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.NumberConstant;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.service.erpDataSync.EmployeeService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.DateUtil;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftIService;
import com.digigate.engineeringmanagement.planning.constant.AmlType;
import com.digigate.engineeringmanagement.planning.constant.OilRecordTypeEnum;
import com.digigate.engineeringmanagement.planning.constant.SignatureTypeEnum;
import com.digigate.engineeringmanagement.planning.dto.SectorWiseUtilizationPairDto;
import com.digigate.engineeringmanagement.planning.dto.request.OilUpLiftReportSearchDto;
import com.digigate.engineeringmanagement.planning.entity.AircraftMaintenanceLog;
import com.digigate.engineeringmanagement.planning.entity.AircraftMaintenanceLogSignature;
import com.digigate.engineeringmanagement.planning.entity.Airport;
import com.digigate.engineeringmanagement.planning.entity.AmlOilRecord;
import com.digigate.engineeringmanagement.planning.payload.dto.request.MultipleDailyHrsReportSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.*;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.AircraftMaintenanceLogRepository;
import com.digigate.engineeringmanagement.planning.repository.AmlFlightDataRepository;
import com.digigate.engineeringmanagement.planning.repository.AmlOilRecordRepository;
import com.digigate.engineeringmanagement.planning.service.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Aircraft maintenance log service implementation
 *
 * @author Pranoy Das
 */
@Service
public class AircraftMaintenanceLogServiceImpl
        extends AbstractSearchService<AircraftMaintenanceLog, AircraftMaintenanceLogDto, AmlSearchDto>
        implements AircraftMaintenanceLogService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AircraftMaintenanceLogServiceImpl.class);
    private static final String AML_AIRCRAFT_ID = "amlAircraftId";
    private static final String PAGE_NO = "pageNo";
    private static final String ALPHABET = "alphabet";
    private static final String FLIGHT_NO = "flightNo";
    private static final String DATE = "date";

    private static final int MAX_DATE_DIFFERENCE = 30;
    private static final String FROM_AIRPORT_ID = "fromAirportId";
    private static final String TO_AIRPORT = "toAirport";
    private static final String IS_ACTIVE = "isActive";
    private static final int DEFAULT_INVALID_PAGE_NO = -1;
    private static final String COLON_SEPARATOR = ":";
    private static final String VOID_AML = "VOID";
    private static final String NIL_AML = "NIL";
    private static final String MAINT_AML = "MAINT";
    private static final String DEFAULT_TIME_STR = "0:00";
    private static final double DEFAULT_DOUBLE_VALUE = 0.00;
    private static final int DEFAULT_INTEGER_VALUE = 0;
    private static final String CREATED_AT = "createdAt";

    private static final String sectorDivider = ":";
    private final AircraftMaintenanceLogRepository aircraftMaintenanceLogRepository;
    private final AircraftIService aircraftService;
    private final AirportService airportService;
    private final EmployeeService employeeService;
    private final IAmlBookService amlBookService;

    //TODO - need to create a different class to avoid repository dependency of other class
    private final AmlOilRecordRepository amlOilRecordRepository;
    private final AmlFlightDataIService amlFlightDataIService;
    private final AmlOilRecordIService amlOilRecordIService;
    private final AmlDefectRectificationService amlDefectRectificationService;

    private final AmlFlightDataRepository amlFlightDataRepository;

    /**
     * Autowired constructor
     *
     * @param repository                       {@link AbstractRepository}
     * @param aircraftMaintenanceLogRepository {@link AircraftMaintenanceLogRepository}
     * @param aircraftService                  {@link AircraftIService}
     * @param airportService                   {@link AirportService}
     * @param employeeService                  {@link EmployeeService}
     * @param amlBookService                   {@link IAmlBookService}
     * @param amlOilRecordRepository           {@link AmlOilRecordRepository}
     * @param amlFlightDataIService            {@link AmlFlightDataIService}
     * @param amlOilRecordIService             {@link AmlOilRecordIService}
     * @param amlDefectRectificationService    {@link AmlDefectRectificationService}
     * @param amlFlightDataRepository
     */
    @Autowired
    public AircraftMaintenanceLogServiceImpl(AbstractRepository<AircraftMaintenanceLog> repository,
                                             AircraftMaintenanceLogRepository aircraftMaintenanceLogRepository,
                                             AircraftIService aircraftService, AirportService airportService,
                                             EmployeeService employeeService,
                                             IAmlBookService amlBookService,
                                             AmlOilRecordRepository amlOilRecordRepository,
                                             @Lazy AmlFlightDataIService amlFlightDataIService,
                                             @Lazy AmlOilRecordIService amlOilRecordIService,
                                             @Lazy AmlDefectRectificationService amlDefectRectificationService,
                                             AmlFlightDataRepository amlFlightDataRepository) {
        super(repository);
        this.aircraftMaintenanceLogRepository = aircraftMaintenanceLogRepository;
        this.aircraftService = aircraftService;
        this.airportService = airportService;
        this.employeeService = employeeService;
        this.amlOilRecordRepository = amlOilRecordRepository;
        this.amlBookService = amlBookService;
        this.amlFlightDataIService = amlFlightDataIService;
        this.amlOilRecordIService = amlOilRecordIService;
        this.amlDefectRectificationService = amlDefectRectificationService;
        this.amlFlightDataRepository = amlFlightDataRepository;
    }

    public FlightDataInfoViewModel getFlightDataInfoByDate(LocalDate date, Long aircraftId) {
        return amlFlightDataRepository.findAmlFlightDataByDate(date, aircraftId);
    }

    /**
     * responsible for finding all active aml
     *
     * @return aml as view model
     */
    @Override
    public List<AmlDropdownViewModel> getAllActiveAml() {
        return aircraftMaintenanceLogRepository.findAllActiveAml();
    }

    /**
     * This method is responsible for validating page no of aml
     *
     * @param pageNoDto page info as view model
     */
    @Override
    public void validateAmlPageNo(PageNoDto pageNoDto) {
        if (Objects.nonNull(pageNoDto.getAlphabet()) &&
                !(pageNoDto.getAlphabet() >= 'A' && pageNoDto.getAlphabet() <= 'Z')) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_ALPHABET, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        List<AmlLastPageAndAircraftInfo> pages =
                aircraftMaintenanceLogRepository.findAllAmlByPageNo(pageNoDto.getPageNo(), pageNoDto.getAircraftId());

        if (Objects.nonNull(pageNoDto.getAlphabet()) && CollectionUtils.isNotEmpty(pages)) {
            AmlLastPageAndAircraftInfo amlLastPageAndAircraftInfo = pages.get(pages.size() - 1);

            if (Objects.nonNull(amlLastPageAndAircraftInfo.getAlphabet())
                    && pageNoDto.getAlphabet() <= amlLastPageAndAircraftInfo.getAlphabet()) {
                throw new EngineeringManagementServerException(
                        ErrorId.INVALID_AML_ALPHABET, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
                );
            }
        }

        if (CollectionUtils.isNotEmpty(
                aircraftMaintenanceLogRepository.findAllAmlIdsByPageNoAndAircraftAndAlphabet(pageNoDto.getPageNo(),
                        pageNoDto.getAircraftId(), pageNoDto.getAlphabet()))) {
            throw new EngineeringManagementServerException(
                    ErrorId.PAGE_NO_ALREADY_EXITS, HttpStatus.UNPROCESSABLE_ENTITY,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        Integer pageNo = pageNoDto.getPageNo();

        Optional<Integer> startPageNo =
                amlBookService.findAmlBookByAircraftAndPageNumber(pageNoDto.getAircraftId(), pageNo);


        if (startPageNo.isEmpty()) {
            throw new EngineeringManagementServerException(ErrorId.PAGE_NUMBER_IS_NOT_FOUND_IN_ANY_BOOK,
                    HttpStatus.UNPROCESSABLE_ENTITY, MDC.get(ApplicationConstant.TRACE_ID));
        }

        if (Objects.nonNull(pageNoDto.getAlphabet())) {
            Set<Long> amlIds =
                    aircraftMaintenanceLogRepository.findAllByAircraftIdAndPageNo(pageNoDto.getAircraftId(), pageNo);

            if (CollectionUtils.isEmpty(amlIds)) {
                throw new EngineeringManagementServerException(ErrorId.INVALID_PAGE_NO,
                        HttpStatus.UNPROCESSABLE_ENTITY, MDC.get(ApplicationConstant.TRACE_ID));
            }

            return;
        }

        AmlLastPageAndAircraftInfo amlLastPageAndAircraftInfo =
                findAircraftInfoAndPreviousAmlPageNo(pageNoDto.getAircraftId());

        Integer highestPageNo =
                NumberUtil.getDefaultIfNull(amlLastPageAndAircraftInfo.getPageNo(), DEFAULT_INVALID_PAGE_NO);

        if (highestPageNo.equals(DEFAULT_INVALID_PAGE_NO)) {
            if (!startPageNo.get().equals(pageNo)) {
                throw new EngineeringManagementServerException(ErrorId.INVALID_PAGE_NO,
                        HttpStatus.UNPROCESSABLE_ENTITY, MDC.get(ApplicationConstant.TRACE_ID));
            }
        } else {
            if (!pageNo.equals(highestPageNo + 1) && !startPageNo.get().equals(pageNo)) {
                throw new EngineeringManagementServerException(ErrorId.INVALID_PAGE_NO,
                        HttpStatus.UNPROCESSABLE_ENTITY, MDC.get(ApplicationConstant.TRACE_ID));
            }
        }
    }

    /**
     * Build specification by criteria
     *
     * @param searchDto {@link AmlSearchDto}
     * @return {@link Specification}
     */
    @Override
    protected Specification<AircraftMaintenanceLog> buildSpecification(AmlSearchDto searchDto) {
        CustomSpecification<AircraftMaintenanceLog> customSpecification = new CustomSpecification<>();

        return Specification.where(
                customSpecification.equalSpecificationAtRoot(searchDto.getAircraftId(), AML_AIRCRAFT_ID)
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getPageNo(), PAGE_NO))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getAlphabet(), ALPHABET))
                        .and(customSpecification.likeSpecificationAtRoot(searchDto.getFlightNo(), FLIGHT_NO))
                        .and(customSpecification
                                .inBetweenSpecification(searchDto.getFromDate(), searchDto.getToDate(), DATE))
                        .and(customSpecification
                                .equalSpecificationAtRoot(searchDto.getFromAirportId(), FROM_AIRPORT_ID))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getToAirportId(), TO_AIRPORT))
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE))
        );
    }

    /**
     * responsible for convert aml entity to view model
     *
     * @param aircraftMaintenanceLog {@link AircraftMaintenanceLog}
     * @return AircraftMaintenanceLog as view model
     */
    @Override
    protected AircraftMaintenanceLogViewModel convertToResponseDto(AircraftMaintenanceLog aircraftMaintenanceLog) {
        Set<Long> airportIds = new HashSet<>();
        airportIds.add(aircraftMaintenanceLog.getFromAirportId());
        airportIds.add(aircraftMaintenanceLog.getToAirportId());
        airportIds.add(aircraftMaintenanceLog.getPreFlightInspectionAirportId());

        List<Airport> airportList = airportService.findByIds(airportIds);
        Map<Long, String> airportMap = new HashMap<>();

        airportList.forEach(
                airport -> {
                    if (!airportMap.containsKey(airport.getId())) {
                        airportMap.put(airport.getId(), airport.getIataCode());
                    }
                }
        );

        Set<Long> employeeIds = new HashSet<>();
        employeeIds.add(aircraftMaintenanceLog.getCaptainId());
        employeeIds.add(aircraftMaintenanceLog.getFirstOfficerId());

        Set<AircraftMaintenanceLogSignature> aircraftMaintenanceLogSignatures =
                aircraftMaintenanceLog.getAircraftMaintenanceLogSignatures();
        List<AmlSignatureViewModel> amlSignatureViewModels = new ArrayList<>();

        aircraftMaintenanceLogSignatures.forEach(amlSignature -> {
            amlSignatureViewModels.add(
                    AmlSignatureViewModel.builder()
                            .amlSignatureId(amlSignature.getId())
                            .signatureId(Objects.nonNull(amlSignature.getSignature())
                                    ? amlSignature.getSignature().getId() : null)
                            .authNo(Objects.nonNull(amlSignature.getSignature()) ?
                                    amlSignature.getSignature().getAuthNo() : null)
                            .signatureType(amlSignature.getSignatureType())
                            .airportId(amlSignature.getSignatureType()
                                    .equals(SignatureTypeEnum.CERTIFICATION_FOR_PFI.getSignatureType())
                                    ? aircraftMaintenanceLog.getPreFlightInspectionAirportId() : null)
                            .airportName(amlSignature.getSignatureType()
                                    .equals(SignatureTypeEnum.CERTIFICATION_FOR_PFI.getSignatureType())
                                    ? airportMap.get(aircraftMaintenanceLog.getPreFlightInspectionAirportId()) : null)
                            .pfiDate(amlSignature.getSignatureType()
                                    .equals(SignatureTypeEnum.CERTIFICATION_FOR_PFI.getSignatureType())
                                    ? aircraftMaintenanceLog.getPfiTime() : null)
                            .ocaDate(amlSignature.getSignatureType()
                                    .equals(SignatureTypeEnum.CERTIFICATION_FOR_FLT.getSignatureType())
                                    ? aircraftMaintenanceLog.getPfiTime() : null)
                            .employeeId(Objects.nonNull(amlSignature.getSignature()) ?
                                    amlSignature.getSignature().getEmployeeId() : null)
                            .build()
            );
            if (Objects.nonNull(amlSignature.getSignature())) {
                employeeIds.add(amlSignature.getSignature().getEmployeeId());
            }
        });

        List<Employee> employeeList = employeeService.getAllByDomainIdIn(employeeIds, true);
        Map<Long, String> employeeMap = new HashMap<>();

        employeeList.forEach(
                employee -> {
                    if (!employeeMap.containsKey(employee.getId())) {
                        employeeMap.put(employee.getId(), employee.getName());
                    }
                }
        );

        amlSignatureViewModels.forEach(amlSignatureViewModel ->
                amlSignatureViewModel.setSignatureName(employeeMap.get(amlSignatureViewModel.getEmployeeId())));

        AmlFlightViewModel amlFlightDataViewModel = amlFlightDataIService.findByAmlId(aircraftMaintenanceLog.getId());
        List<AmlOilRecordDto> amlOilRecordViewModels =
                amlOilRecordIService.getOilRecordByAmlId(new OilRecordSearchDto(aircraftMaintenanceLog.getId(), true));
        List<AmlDefectRectificationModelView> rectificationViewModels =
                amlDefectRectificationService.getDefectRectificationsByAmlId(aircraftMaintenanceLog.getId());

        return AircraftMaintenanceLogViewModel.builder()
                .aircraftMaintenanceLogId(aircraftMaintenanceLog.getId())
                .aircraftId(Objects.nonNull(aircraftMaintenanceLog.getAircraft()) ?
                        aircraftMaintenanceLog.getAircraft().getId() : null)
                .aircraftName(Objects.nonNull(aircraftMaintenanceLog.getAircraft()) ?
                        aircraftMaintenanceLog.getAircraft().getAircraftName() : null)
                .fromAirportId(aircraftMaintenanceLog.getFromAirportId())
                .fromAirportIataCode(airportMap.get(aircraftMaintenanceLog.getFromAirportId()))
                .preFlightInspectionAirportId(aircraftMaintenanceLog.getPreFlightInspectionAirportId())
                .preFlightInspectionIataCode(airportMap.get(aircraftMaintenanceLog.getPreFlightInspectionAirportId()))
                .toAirportId(aircraftMaintenanceLog.getToAirportId())
                .toAirportIataCode(airportMap.get(aircraftMaintenanceLog.getToAirportId()))
                .captainId(aircraftMaintenanceLog.getCaptainId())
                .captainName(employeeMap.get(aircraftMaintenanceLog.getCaptainId()))
                .firstOfficerId(aircraftMaintenanceLog.getFirstOfficerId())
                .firstOfficerName(employeeMap.get(aircraftMaintenanceLog.getFirstOfficerId()))
                .pageNo(aircraftMaintenanceLog.getPageNo())
                .alphabet(aircraftMaintenanceLog.getAlphabet())
                .flightNo(aircraftMaintenanceLog.getFlightNo())
                .date(aircraftMaintenanceLog.getDate())
                .pfiTime(aircraftMaintenanceLog.getPfiTime())
                .ocaTime(aircraftMaintenanceLog.getOcaTime())
                .refuelDelivery(aircraftMaintenanceLog.getRefuelDelivery())
                .specificGravity(aircraftMaintenanceLog.getSpecificGravity())
                .convertedIn(aircraftMaintenanceLog.getConvertedIn())
                .remarks(aircraftMaintenanceLog.getRemarks())
                .amlType(aircraftMaintenanceLog.getAmlType())
                .isActive(aircraftMaintenanceLog.getIsActive())
                .signatureList(amlSignatureViewModels)
                .amlFlightDataViewModel(amlFlightDataViewModel)
                .amlOilRecordViewModels(amlOilRecordViewModels)
                .rectificationViewModels(rectificationViewModels)
                .build();
    }

    private DailyHrsReportAircraftModel getDailyHrsHeaderModel(Long aircraftId, LocalDate date, DailyHrsReportTotalModel total) {
        return aircraftService.findDailyHrsReportAircraftModelByAircraftById(aircraftId, date, total);
    }

    private Page<AircraftMaintenanceLog> getDailyHrsDataModel(LocalDate date, Long aircraftId, Pageable pageable) {
        return aircraftMaintenanceLogRepository.findByDateAndAircraftId(date, aircraftId, pageable);
    }

    private DailyHrsReportDataModel convertToDailyHrsReportDataModel(AircraftMaintenanceLog maintenanceLog) {
        DailyHrsReportDataModel dataModels = new DailyHrsReportDataModel();
        StringBuilder pageBuilder = new StringBuilder();
        pageBuilder.append(maintenanceLog.getPageNo());

        if (Objects.nonNull(maintenanceLog.getAlphabet())) {
            pageBuilder.append(maintenanceLog.getAlphabet());
        }

        dataModels.setAmlId(maintenanceLog.getId());
        dataModels.setDate((maintenanceLog.getDate()));
        dataModels.setFlightNo(maintenanceLog.getFlightNo());
        dataModels.setPageNo(pageBuilder.toString());
        setAmlType(maintenanceLog, dataModels);
        prepareFlightDataForDailyHoursReport(maintenanceLog, dataModels);
        return dataModels;
    }

    private void setAmlType(AircraftMaintenanceLog maintenanceLog, DailyHrsReportDataModel dataModels) {
        if (maintenanceLog.getAmlType().equals(AmlType.REGULAR)) {
            StringBuilder sectorBuilder = new StringBuilder();
            if (Objects.nonNull(maintenanceLog.getFromAirport())) {
                sectorBuilder.append(maintenanceLog.getFromAirport().getIataCode());
            }
            if (Objects.nonNull(maintenanceLog.getToAirport())) {
                sectorBuilder.append(COLON_SEPARATOR + "").append(maintenanceLog.getToAirport().getIataCode());
            }
            dataModels.setSector(sectorBuilder.toString());
        } else {
            if (maintenanceLog.getAmlType().equals(AmlType.VOID)) {
                dataModels.setSector(VOID_AML);
            } else if (maintenanceLog.getAmlType().equals(AmlType.NIL)) {
                dataModels.setSector(NIL_AML);
            } else {
                dataModels.setSector(MAINT_AML);
            }
        }
    }

    private void prepareFlightDataForDailyHoursReport(AircraftMaintenanceLog maintenanceLog, DailyHrsReportDataModel dataModels) {
        if (Objects.nonNull(maintenanceLog.getFlightData())) {
            dataModels.setBlockOffTime(convertTimeToString(maintenanceLog.getFlightData().getBlockOffTime()));
            dataModels.setBlockOnTime(convertTimeToString(maintenanceLog.getFlightData().getBlockOnTime()));
            dataModels.setBlockTime(NumberUtil
                    .getDefaultIfNull(maintenanceLog.getFlightData().getBlockTime(), DEFAULT_DOUBLE_VALUE));
            dataModels.setTakeOffTime(convertTimeToString(maintenanceLog.getFlightData().getTakeOffTime()));
            dataModels.setLandingTime(convertTimeToString(maintenanceLog.getFlightData().getLandingTime()));
            dataModels.setNoOfLanding(NumberUtil
                    .getDefaultIfNull(maintenanceLog.getFlightData().getNoOfLanding(), DEFAULT_INTEGER_VALUE));
            dataModels.setAirTime(NumberUtil
                    .getDefaultIfNull(maintenanceLog.getFlightData().getAirTime(), DEFAULT_DOUBLE_VALUE));
            dataModels.setGrandTotalAirTime(NumberUtil
                    .getDefaultIfNull(maintenanceLog.getFlightData().getGrandTotalAirTime(), DEFAULT_DOUBLE_VALUE));
            dataModels.setGrandTotalLanding(NumberUtil
                    .getDefaultIfNull(maintenanceLog.getFlightData().getGrandTotalLanding(), DEFAULT_INTEGER_VALUE));
        }
    }

    @Override
    public DailyFlyingHoursReportViewModel getDailyHrsReport(LocalDate date, Long aircraftId,
                                                             Integer page, Integer size) {
        DailyFlyingHoursReportViewModel reportViewModel = new DailyFlyingHoursReportViewModel();

        page = NumberUtil.getValidPageNumber(page);
        size = NumberUtil.getValidPageSize(size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(PAGE_NO).ascending().and(Sort.by(ALPHABET).ascending()));

        Page<AircraftMaintenanceLog> aircraftMaintenanceLogPage = getDailyHrsDataModel(date, aircraftId, pageable);
        List<AircraftMaintenanceLog> aircraftMaintenanceLogs = aircraftMaintenanceLogPage.getContent();

        if (CollectionUtils.isNotEmpty(aircraftMaintenanceLogs)) {
            boolean isBfAdded = false;
            List<DailyHrsReportDataModel> dataModels = new ArrayList<>();

            for (AircraftMaintenanceLog aircraftMaintenanceLog : aircraftMaintenanceLogs) {
                if (!isBfAdded) {
                    reportViewModel.setDailyHrsReportBfDto(
                            DailyHrsReportBfDto.builder()
                                    .grandTotalAirTime(aircraftMaintenanceLog.getFlightData().getTotalAirTime())
                                    .grandTotalLanding(aircraftMaintenanceLog.getFlightData().getTotalLanding())
                                    .build()
                    );
                    isBfAdded = true;
                }

                dataModels.add(convertToDailyHrsReportDataModel(aircraftMaintenanceLog));
            }

            Set<Long> amlIds = dataModels.stream()
                    .map(DailyHrsReportDataModel::getAmlId).collect(Collectors.toSet());
            Map<Long, AmlOilRecord> amlOilRecordMap = getOilRecordMap(amlIds);

            dataModels.forEach(dailyHrsReportDataModel -> {
                if (amlOilRecordMap.containsKey(dailyHrsReportDataModel.getAmlId())) {
                    addOilRecord(amlOilRecordMap.get(dailyHrsReportDataModel.getAmlId()), dailyHrsReportDataModel);
                } else {
                    dailyHrsReportDataModel.setEngineOil1(DEFAULT_DOUBLE_VALUE);
                    dailyHrsReportDataModel.setEngineOil2(DEFAULT_DOUBLE_VALUE);
                    dailyHrsReportDataModel.setApuOil(DEFAULT_DOUBLE_VALUE);
                }
            });

            DailyHrsReportTotalModel total = new DailyHrsReportTotalModel();
            prepareTotal(dataModels, total);
            reportViewModel.setTotal(total);
            reportViewModel.setDailyHrsReportAircraftModel(getDailyHrsHeaderModel(aircraftId, date, total));

            PageData pageData = new PageData(dataModels,
                    aircraftMaintenanceLogPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    aircraftMaintenanceLogPage.getTotalElements());
            reportViewModel.setPageData(pageData);
        }

        return reportViewModel;
    }


    private void addOilRecord(AmlOilRecord amlOilRecord, DailyHrsReportDataModel dailyHrsReportDataModel) {
        dailyHrsReportDataModel.setEngineOil1(amlOilRecord.getEngineOil1());
        dailyHrsReportDataModel.setEngineOil2(amlOilRecord.getEngineOil2());
        dailyHrsReportDataModel.setApuOil(amlOilRecord.getApuOil());
    }

    private Map<Long, AmlOilRecord> getOilRecordMap(Set<Long> amlIds) {
        if (CollectionUtils.isEmpty(amlIds)) {
            return Collections.emptyMap();
        }
        return amlOilRecordRepository.findByAmlIdAndType(amlIds, OilRecordTypeEnum.UPLIFT).stream()
                .collect(Collectors.toMap(AmlOilRecord::getAmlId, Function.identity()));
    }

    private void prepareTotal(List<DailyHrsReportDataModel> dataModelList, DailyHrsReportTotalModel total) {
        int sumOfNoOfLanding = 0;
        double sumOfTotalAirTime = 0.0;
        double sumOfEngineOil1 = 0.0d;
        double sumOfApuOil = 0.0d;
        double sumOfEngineOil2 = 0.0d;
        double grandTotalAirTime = Double.MIN_VALUE;
        int grandTotalLanding = Integer.MIN_VALUE;

        for (DailyHrsReportDataModel dataModel : dataModelList) {
            sumOfNoOfLanding += Objects.nonNull(dataModel.getNoOfLanding()) ? dataModel.getNoOfLanding() : 0;
            sumOfTotalAirTime = DateUtil.addTimes(sumOfTotalAirTime,
                    (Objects.nonNull(dataModel.getAirTime()) ? dataModel.getAirTime() : 0.0));
            sumOfEngineOil1 = sumOfEngineOil1 +
                    (Objects.nonNull(dataModel.getEngineOil1()) ? dataModel.getEngineOil1() : 0.0);
            sumOfApuOil = sumOfApuOil + (Objects.nonNull(dataModel.getApuOil()) ? dataModel.getApuOil() : 0.0);
            sumOfEngineOil2 = sumOfEngineOil2 +
                    (Objects.nonNull(dataModel.getEngineOil2()) ? dataModel.getEngineOil2() : 0.0);

            grandTotalAirTime = Math.max(NumberUtil.getDefaultIfNull(dataModel.getGrandTotalAirTime(), 0.0d),
                    grandTotalAirTime);
            grandTotalLanding = Math.max(NumberUtil.getDefaultIfNull(dataModel.getGrandTotalLanding(), 0),
                    grandTotalLanding);
        }

        total.setNoOfLanding(sumOfNoOfLanding);
        total.setTotalAirTime(sumOfTotalAirTime);
        total.setEngineOil1(sumOfEngineOil1);
        total.setApuOil(sumOfApuOil);
        total.setEngineOil2(sumOfEngineOil2);
        total.setGrandTotalAirTime(grandTotalAirTime);
        total.setGrandTotalLanding(grandTotalLanding);
    }

    /**
     * This method is responsible for generate Utilization Report
     *
     * @param searchDto {@link  UtilizationReportSearchDto}
     * @return {@link UtilizationReportResponse}
     */
    @Override
    public UtilizationReportResponse getUtilizationReport(UtilizationReportSearchDto searchDto) {
        UtilizationReportResponse utilizationReportData = new UtilizationReportResponse();
        aircraftService.utilizationReportHeader(searchDto.getAircraftId(), utilizationReportData);
        List<SectorWiseUtilizationReportDto> reportDtoList =
                aircraftMaintenanceLogRepository.findAllAmlByAircraftIdAndDate(searchDto.getAircraftId(),
                        searchDto.getFromDate(), searchDto.getToDate());
        if (CollectionUtils.isNotEmpty(reportDtoList)) {
            prepareUtilizationReportData(reportDtoList, utilizationReportData);
            prepareUtilizationTotal(reportDtoList, utilizationReportData);
        }
        generatePreviousThreeMonthTotal(searchDto, utilizationReportData);
        return utilizationReportData;
    }

    /**
     * responsible for finding highest page no of aml
     *
     * @return highest page no of aml
     */
    @Override
    public AmlLastPageAndAircraftInfo findAircraftInfoAndLastAmlPageNo(Long aircraftId) {
        AmlLastPageAndAircraftInfo amlLastPageAndAircraftInfo = aircraftService.findAircraftInfo(aircraftId);

        if (Objects.nonNull(amlLastPageAndAircraftInfo.getTotalApuHours())) {
            if (amlLastPageAndAircraftInfo.getTotalApuHours() < 0) {
                amlLastPageAndAircraftInfo.setIsApuControl(false);
            }
        }
        if (Objects.isNull(amlLastPageAndAircraftInfo)) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_AIRCRAFT, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

//        AmlFlightDataVerify amlFlightDataVerify = aircraftMaintenanceLogRepository.findMaxAmlPageWithFlightDataAndAmlType(
//                aircraftId);
//
//        if (Objects.nonNull(amlFlightDataVerify)) {
//            if (Objects.isNull(amlFlightDataVerify.getFlightDataId())) {
//                aircraftMaintenanceLogRepository.deleteById(amlFlightDataVerify.getAmlId());
//                throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(
//                        ErrorId.AML_REMOVED_DUE_TO_INVALID_FLIGHT_DATA,
//                        AircraftMaintenanceLog.class.getSimpleName()));
//
//            }
//        }

        AmlDropdownViewModel amlDropdownViewModel =
                aircraftMaintenanceLogRepository.findMaxAmlPageByAircraftId(aircraftId);

        if (Objects.nonNull(amlDropdownViewModel)) {
            AmlDropdownViewModel lastPage =
                    aircraftMaintenanceLogRepository.findMaxAmlPageWithoutAlphabetByAircraftId(aircraftId);
            amlLastPageAndAircraftInfo.setMaxPageNo(lastPage.getPageNo());
            amlLastPageAndAircraftInfo.setPageNo(amlDropdownViewModel.getPageNo());
            amlLastPageAndAircraftInfo.setAlphabet(amlDropdownViewModel.getAlphabet());
        }

        return amlLastPageAndAircraftInfo;
    }

    /**
     * This Method will prepare total value for Sector-wise Utilization
     *
     * @param reportDtoList             {@link  SectorWiseUtilizationReportDto}
     * @param utilizationReportResponse {@link  UtilizationReportResponse}
     */
    public void prepareUtilizationTotal(List<SectorWiseUtilizationReportDto> reportDtoList,
                                        UtilizationReportResponse utilizationReportResponse) {
        double sumOfTotalHours = 0.0;
        int sumOfTotalCycle = 0;
        for (SectorWiseUtilizationReportDto utilizationReportDto : reportDtoList) {
            sumOfTotalHours = DateUtil.addTimes(sumOfTotalHours, Objects.nonNull(utilizationReportDto.getTotalHours()) ?
                    utilizationReportDto.getTotalHours() : 0.0d);
            sumOfTotalCycle += Objects.nonNull(utilizationReportDto.getTotalCycle()) ?
                    utilizationReportDto.getTotalCycle() : 0;
        }
        utilizationReportResponse.setTotalHours(NumberUtil.parseValue(NumberUtil.formatDecimalValue(sumOfTotalHours,
                NumberConstant.TWO_DECIMAL_FORMAT), Double.class));
        utilizationReportResponse.setTotalCycle(sumOfTotalCycle);
    }


    /**
     * This method generates Total report of sector-wise Utilization
     *
     * @param reportDtoList         {@link SectorWiseUtilizationReportDto}
     * @param utilizationReportData {@link UtilizationReportResponse}
     */
    public void prepareUtilizationReportData(List<SectorWiseUtilizationReportDto> reportDtoList,
                                             UtilizationReportResponse utilizationReportData) {
        List<SectorWiseUtilizationReportDto> utilizationReportDtoList = new ArrayList<>();
        for (SectorWiseUtilizationReportDto reportDto : reportDtoList) {
            if (StringUtils.isBlank(reportDto.getFlightNo())) {
                reportDto.setFlightNo(null);
            }
            String sector = reportDto.getFromAirportIataCode() + sectorDivider + reportDto.getToAirportIataCode();
            reportDto.setSector(sector);
            utilizationReportDtoList.add(reportDto);
        }

        Map<SectorWiseUtilizationPairDto, List<SectorWiseUtilizationReportDto>> sectorWiseUtilizationPairDtoListMap =
                utilizationReportDtoList.stream()
                        .sorted(Comparator.comparing(
                                sectorWiseUtilizationReportDto -> sectorWiseUtilizationReportDto.getFlightNo(),
                                Comparator.nullsLast(Comparator.naturalOrder())))
                        .collect(Collectors.groupingBy(s ->
                                SectorWiseUtilizationPairDto.builder()
                                        .sector(s.getSector())
                                        .flightNo(s.getFlightNo())
                                        .build(), LinkedHashMap::new, Collectors.toList())
                        );

        List<SectorWiseUtilizationReportDto> sectorWiseUtilizationReportDtoList =
                sectorWiseUtilizationPairDtoListMap.keySet().stream()
                        .map(key -> {
                            List<SectorWiseUtilizationReportDto> reportDtos =
                                    sectorWiseUtilizationPairDtoListMap.get(key);
                            return SectorWiseUtilizationReportDto.builder()
                                    .flightNo(key.getFlightNo())
                                    .sector(key.getSector())
                                    .totalCycle(reportDtos.stream()
                                            .filter(sectorWiseUtilizationReportDto ->
                                                    Objects.nonNull(sectorWiseUtilizationReportDto.getTotalCycle()))
                                            .mapToInt(SectorWiseUtilizationReportDto::getTotalCycle)
                                            .sum()
                                    )
                                    .totalHours(
                                            calculateTotalHour(reportDtos)
                                    ).build();
                        }).collect(Collectors.toList());

        utilizationReportData.setUtilizationReportDtoList(sectorWiseUtilizationReportDtoList);
    }

    private Double calculateTotalHour(List<SectorWiseUtilizationReportDto> reportDtos) {
        double totalHour = 0.0;

        for (SectorWiseUtilizationReportDto reportDto : reportDtos) {
            if (Objects.nonNull(reportDto.getTotalHours())) {
                totalHour = DateUtil.addTimes(totalHour, reportDto.getTotalHours());
            }
        }

        return NumberUtil.parseDoubleValue(NumberUtil.formatDecimalValue(totalHour, NumberConstant.TWO_DECIMAL_FORMAT));
    }

    /**
     * This method will generate total data for previous three month
     *
     * @param searchDto             {@link UtilizationReportSearchDto}
     * @param utilizationReportData {@link UtilizationReportResponse}
     */
    public void generatePreviousThreeMonthTotal(UtilizationReportSearchDto searchDto,
                                                UtilizationReportResponse utilizationReportData) {

        for (int previousMonth = 0; previousMonth < 3; previousMonth++) {
            double sumOfTotalHours = 0.0;
            int sumOfTotalCycle = 0;
            YearMonth yearMonth = YearMonth.of(searchDto.getFromDate().minusMonths(previousMonth + 1).getYear(),
                    searchDto.getToDate().minusMonths(previousMonth + 1).getMonth());
            LocalDate firstDayOfMonth = yearMonth.atDay(1);
            LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

            List<SectorWiseUtilizationReportDto> reportDtoList =
                    aircraftMaintenanceLogRepository.findAllAmlByAircraftIdAndDate(searchDto.getAircraftId(),
                            firstDayOfMonth, lastDayOfMonth);
            for (SectorWiseUtilizationReportDto utilizationReportDto : reportDtoList) {
                sumOfTotalHours += Objects.nonNull(utilizationReportDto.getTotalHours()) ?
                        utilizationReportDto.getTotalHours() : 0.0d;
                sumOfTotalCycle += Objects.nonNull(utilizationReportDto.getTotalCycle()) ?
                        utilizationReportDto.getTotalCycle() : 0;
            }

            switch (previousMonth) {
                case 0:
                    utilizationReportData.setPreviousOneMonth(
                            new UtilizationReportTotalView(sumOfTotalHours, sumOfTotalCycle, yearMonth.getMonth().name() + "-" + yearMonth.getYear()));
                case 1:
                    utilizationReportData.setPreviousSecondMonth(
                            new UtilizationReportTotalView(sumOfTotalHours, sumOfTotalCycle, yearMonth.getMonth().name() + "-" + yearMonth.getYear()));
                case 2:
                    utilizationReportData.setPreviousThirdMonth(
                            new UtilizationReportTotalView(sumOfTotalHours, sumOfTotalCycle, yearMonth.getMonth().name() + "-" + yearMonth.getYear()));
            }

        }
    }

    /**
     * This method is responsible for generate oil uplift report
     *
     * @param fromDate   {@link OilUpLiftReportSearchDto}
     * @param toDate     {@link OilUpLiftReportSearchDto}
     * @param aircraftId {@link OilUpLiftReportSearchDto}
     * @return 0ilUpLiftReportViewModel  {@link  OilUpLiftReportViewModel}
     */
    @Override
    public Page<OilUpLiftReportViewModel> getOilUpLiftReport(LocalDate fromDate, LocalDate toDate, Long aircraftId,
                                                             Pageable pageable) {
        Page<OilUpLiftReportViewModel> oilUpLiftReportViewModelList = aircraftMaintenanceLogRepository
                .findAllByAmlAircraftIdAndDate(aircraftId, fromDate, toDate, pageable);
        processOilUpLiftReportViewModelList(oilUpLiftReportViewModelList);
        calculateFuelConsumption(oilUpLiftReportViewModelList, aircraftId);
        return oilUpLiftReportViewModelList;
    }

    private void processOilUpLiftReportViewModelList(Page<OilUpLiftReportViewModel> oilUpLiftReportViewModelList) {
        //TODO - Refactor this code later for reducing the db query
        if (ObjectUtils.isNotEmpty(oilUpLiftReportViewModelList)) {
            Set<Long> amlIds = oilUpLiftReportViewModelList.stream()
                    .map(OilUpLiftReportViewModel::getAmlId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Set<Long> airportIds = oilUpLiftReportViewModelList.stream()
                    .flatMap(amlOilUp -> Stream.of(amlOilUp.getFromAirportId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            List<AmlOilRecord> amlOilUpliftRecordList = amlOilRecordIService.findAllByAmlIdInAndType(amlIds);

            List<AmlOilRecord> amlOilOnArrivalRecordList = amlOilRecordIService.findAllByAmlIdInAndTypeOnArrival(amlIds);

            Map<Long, AmlOilRecord> upliftOilRecordMap = amlOilUpliftRecordList.stream()
                    .collect(Collectors.toMap(AmlOilRecord::getAmlId, Function.identity()));
            Map<Long, AmlOilRecord> oilRecordOnArrivalMap = amlOilOnArrivalRecordList.stream()
                    .collect(Collectors.toMap(AmlOilRecord::getAmlId, Function.identity()));

            List<AmlFlightDataForOilUpliftReportViewModel> amlFlightDataForOilUpliftReportViewModelList
                    = amlFlightDataIService.getAllFlightDataByAmlIdIn(amlIds);
            Map<Long, AmlFlightDataForOilUpliftReportViewModel> flightDataForOilUpliftReportViewModelMap
                    = amlFlightDataForOilUpliftReportViewModelList.stream().collect(Collectors
                    .toMap(AmlFlightDataForOilUpliftReportViewModel::getAmlId, Function.identity()));

            List<Airport> airportList = this.airportService.findByIds(airportIds);
            Map<Long, Airport> airportMap = airportList.stream()
                    .collect(Collectors.toMap(Airport::getId, Function.identity()));

            oilUpLiftReportViewModelList.forEach(oilUpLiftReportViewModel -> {
                oilUpLiftReportViewModel.setAirTime((Objects.nonNull(flightDataForOilUpliftReportViewModelMap
                        .get(oilUpLiftReportViewModel.getAmlId()))) ? flightDataForOilUpliftReportViewModelMap
                        .get(oilUpLiftReportViewModel.getAmlId()).getAirTime() : null);

                oilUpLiftReportViewModel.setFromAirport((Objects.nonNull(airportMap.get(oilUpLiftReportViewModel
                        .getFromAirportId()))) ? airportMap.get(oilUpLiftReportViewModel.getFromAirportId())
                        .getIataCode() : null);

                oilUpLiftReportViewModel.setHydOil1((Objects.nonNull(upliftOilRecordMap.get(oilUpLiftReportViewModel
                        .getAmlId()))) ? upliftOilRecordMap.get(oilUpLiftReportViewModel.getAmlId()).getHydOil1() : null);

                oilUpLiftReportViewModel.setHydOil2((Objects.nonNull(upliftOilRecordMap.get(oilUpLiftReportViewModel
                        .getAmlId()))) ? upliftOilRecordMap.get(oilUpLiftReportViewModel.getAmlId()).getHydOil2() : null);

                oilUpLiftReportViewModel.setHydOil3((Objects.nonNull(upliftOilRecordMap.get(oilUpLiftReportViewModel
                        .getAmlId()))) ? upliftOilRecordMap.get(oilUpLiftReportViewModel.getAmlId()).getHydOil3() : null);

                oilUpLiftReportViewModel.setEngineOil1((Objects.nonNull(upliftOilRecordMap.get(oilUpLiftReportViewModel
                        .getAmlId()))) ? upliftOilRecordMap.get(oilUpLiftReportViewModel.getAmlId()).getEngineOil1() : null);

                oilUpLiftReportViewModel.setEngineOil2((Objects.nonNull(upliftOilRecordMap.get(oilUpLiftReportViewModel
                        .getAmlId()))) ? upliftOilRecordMap.get(oilUpLiftReportViewModel.getAmlId()).getEngineOil2() : null);

                oilUpLiftReportViewModel.setApuOil((Objects.nonNull(upliftOilRecordMap.get(oilUpLiftReportViewModel
                        .getAmlId()))) ? upliftOilRecordMap.get(oilUpLiftReportViewModel.getAmlId()).getApuOil() : null);

                oilUpLiftReportViewModel.setUpliftOilRecord((Objects.nonNull(upliftOilRecordMap.get(oilUpLiftReportViewModel
                        .getAmlId()))) ? upliftOilRecordMap.get(oilUpLiftReportViewModel.getAmlId()).getOilRecord() : 0.0);

                oilUpLiftReportViewModel.setOnArrivalRecord((Objects.nonNull(oilRecordOnArrivalMap
                        .get(oilUpLiftReportViewModel.getAmlId()))) ? oilRecordOnArrivalMap.get(oilUpLiftReportViewModel
                        .getAmlId()).getOilRecord() : 0.0);

                oilUpLiftReportViewModel.setTotalOilRecord(oilUpLiftReportViewModel
                        .getUpliftOilRecord() + oilUpLiftReportViewModel.getOnArrivalRecord());
            });
        }
    }

    private void calculateFuelConsumption(Page<OilUpLiftReportViewModel> oilUpLiftReportViewModelList, Long aircraftId) {
        TreeMap<Long, OilUpLiftReportViewModel> oilRecordMap = new TreeMap<>();

        oilUpLiftReportViewModelList.forEach(oilUpLiftReportViewModel -> {
            if (oilUpLiftReportViewModel.getAmlType().equals(AmlType.REGULAR)) {
                oilRecordMap.put(oilUpLiftReportViewModel.getAmlId(), oilUpLiftReportViewModel);
            } else {
                oilUpLiftReportViewModel.setFuelConsumption(0.0);
            }
        });

        for (Map.Entry<Long, OilUpLiftReportViewModel> oilUpLiftReport : oilRecordMap.entrySet()) {
            OilUpLiftReportViewModel currentRecord = oilUpLiftReport.getValue();
            Long nextAmlId = oilRecordMap.higherKey(oilUpLiftReport.getKey());

            if (nextAmlId != null) {
                OilUpLiftReportViewModel nextRecord = oilRecordMap.get(nextAmlId);
                Double onArrivalRecord = nextRecord.getOnArrivalRecord();
                currentRecord.setFuelConsumption(currentRecord.getTotalOilRecord() - onArrivalRecord);
                oilRecordMap.put(currentRecord.getAmlId(), currentRecord);
            } else {
                List<AircraftMaintenanceLog> aircraftMaintenanceLogs = aircraftMaintenanceLogRepository
                        .findNextAmlIds(currentRecord.getAmlId(), aircraftId);
                Long firstAmlId;
                List<AmlOilRecord> amlOilOnArrivalRecordList;

                Optional<AircraftMaintenanceLog> firstRegularAml = aircraftMaintenanceLogs.stream()
                        .filter(aml -> AmlType.REGULAR.equals(aml.getAmlType()))
                        .findFirst();

                if (firstRegularAml.isPresent()) {
                    firstAmlId = firstRegularAml.get().getId();

                    Set<Long> amlIdsSet = Collections.singleton(firstAmlId);
                    amlOilOnArrivalRecordList = amlOilRecordIService.findAllByAmlIdInAndTypeOnArrival(amlIdsSet);

                    if (Objects.nonNull(amlOilOnArrivalRecordList) && !amlOilOnArrivalRecordList.isEmpty()) {
                        Double onArrivalRecord = amlOilOnArrivalRecordList.get(0).getOilRecord();
                        currentRecord.setFuelConsumption(currentRecord.getTotalOilRecord() - onArrivalRecord);
                        oilRecordMap.put(currentRecord.getAmlId(), currentRecord);
                    }
                }
            }
        }

        oilUpLiftReportViewModelList.forEach(oilUpLiftReportViewModel -> {
            if (oilRecordMap.containsKey(oilUpLiftReportViewModel.getAmlId())) {
                OilUpLiftReportViewModel record = oilRecordMap.get(oilUpLiftReportViewModel.getAmlId());
                oilUpLiftReportViewModel.setFuelConsumption(record.getFuelConsumption());
            }
        });
    }


    @Override
    public Boolean verifyAtl(Long amlId) {
        AmlFlightDataVerify flightDataVerify = aircraftMaintenanceLogRepository.findAmlTypeWithFlightDataId(amlId);
        if (Objects.nonNull(flightDataVerify) && Objects.isNull(flightDataVerify.getFlightDataId())) {
            aircraftMaintenanceLogRepository.deleteById(amlId);
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(
                    ErrorId.AML_DATA_NOT_SAVED_DUE_TO_FLIGHT_DATA,
                    AircraftMaintenanceLog.class.getSimpleName()));
        }
        return true;
    }

    @Override
    public List<MultipleDailyFlyingHoursReportViewModel> getMultipleDailyHrsReport
            (MultipleDailyHrsReportSearchDto multipleDailyHrsReportSearchDto) {

        List<MultipleDailyFlyingHoursReportViewModel> multipleDailyFlyingHoursReportViewModelList = new ArrayList<>();
        validateMonthRange(multipleDailyHrsReportSearchDto.getStartDate(), multipleDailyHrsReportSearchDto.getEndDate());
        LocalDate startDate = LocalDate.of(multipleDailyHrsReportSearchDto.getStartDate().getYear()
                , multipleDailyHrsReportSearchDto.getStartDate().getMonthValue()
                , multipleDailyHrsReportSearchDto.getStartDate().getDayOfMonth());
        LocalDate endDate = LocalDate.of(multipleDailyHrsReportSearchDto.getEndDate().getYear()
                , multipleDailyHrsReportSearchDto.getEndDate().getMonthValue()
                , multipleDailyHrsReportSearchDto.getEndDate().getDayOfMonth());
        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            MultipleFlyingHoursReportViewModel multipleFlyingHoursReportViewModel
                    = getMultipleHrsReport(date, multipleDailyHrsReportSearchDto.getAircraftId());
            MultipleDailyFlyingHoursReportViewModel multipleDailyFlyingHoursReportViewModel
                    = new MultipleDailyFlyingHoursReportViewModel();
            if (Objects.nonNull(multipleFlyingHoursReportViewModel.getDailyHrsReportDataModelList())) {
                multipleDailyFlyingHoursReportViewModel.setDate(date);
                multipleDailyFlyingHoursReportViewModel.setMultipleFlyingHoursReportViewModel(multipleFlyingHoursReportViewModel);
                multipleDailyFlyingHoursReportViewModelList.add(multipleDailyFlyingHoursReportViewModel);
            }
        }
        return multipleDailyFlyingHoursReportViewModelList;
    }

    private void validateMonthRange(LocalDate fromDate, LocalDate toDate) {
        if (ChronoUnit.DAYS.between(fromDate, toDate) > MAX_DATE_DIFFERENCE) {
            throw new EngineeringManagementServerException(
                    ErrorId.ONE_MONTH_DATE_RANGE_LIMIT_ERROR, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }

    private MultipleFlyingHoursReportViewModel getMultipleHrsReport(LocalDate date, Long aircraftId) {
        MultipleFlyingHoursReportViewModel multipleFlyingHoursReportViewModel = new MultipleFlyingHoursReportViewModel();
        List<AircraftMaintenanceLog> aircraftMaintenanceLogs = aircraftMaintenanceLogRepository
                .findByDateAndAircraftId(date, aircraftId);

        if (CollectionUtils.isNotEmpty(aircraftMaintenanceLogs)) {
            boolean isBfAdded = false;
            List<DailyHrsReportDataModel> dataModels = new ArrayList<>();

            for (AircraftMaintenanceLog aircraftMaintenanceLog : aircraftMaintenanceLogs) {
                if (!isBfAdded) {
                    multipleFlyingHoursReportViewModel.setDailyHrsReportBfDto(
                            DailyHrsReportBfDto.builder()
                                    .grandTotalAirTime(aircraftMaintenanceLog.getFlightData().getTotalAirTime())
                                    .grandTotalLanding(aircraftMaintenanceLog.getFlightData().getTotalLanding())
                                    .build()
                    );
                    isBfAdded = true;
                }

                dataModels.add(convertToDailyHrsReportDataModel(aircraftMaintenanceLog));
            }

            Set<Long> amlIds = dataModels.stream()
                    .map(DailyHrsReportDataModel::getAmlId).collect(Collectors.toSet());
            Map<Long, AmlOilRecord> amlOilRecordMap = getOilRecordMap(amlIds);

            dataModels.forEach(dailyHrsReportDataModel -> {
                if (amlOilRecordMap.containsKey(dailyHrsReportDataModel.getAmlId())) {
                    addOilRecord(amlOilRecordMap.get(dailyHrsReportDataModel.getAmlId()), dailyHrsReportDataModel);
                } else {
                    dailyHrsReportDataModel.setEngineOil1(DEFAULT_DOUBLE_VALUE);
                    dailyHrsReportDataModel.setEngineOil2(DEFAULT_DOUBLE_VALUE);
                    dailyHrsReportDataModel.setApuOil(DEFAULT_DOUBLE_VALUE);
                }
            });

            DailyHrsReportTotalModel total = new DailyHrsReportTotalModel();
            prepareTotal(dataModels, total);
            multipleFlyingHoursReportViewModel.setTotal(total);
            multipleFlyingHoursReportViewModel.setDailyHrsReportAircraftModel(getDailyHrsHeaderModel(aircraftId, date, total));
            multipleFlyingHoursReportViewModel.setDailyHrsReportDataModelList(dataModels);
        }
        return multipleFlyingHoursReportViewModel;
    }


    @Override
    public List<AircraftMaintenanceLog> findAllNextAmlsWithCurrentAml(Integer pageNo, Long amlAircraftId) {
        return aircraftMaintenanceLogRepository.findAllNextAmlsWithCurrentAml(pageNo, amlAircraftId);
    }

    @Override
    public AmlLastPageAndAircraftInfo findAirframeInfoByPageNo(Integer pageNo, Long aircraftId) {
        return aircraftMaintenanceLogRepository.findAirframeInfoByPageNo(pageNo, aircraftId);
    }

    @Override
    public List<AircraftMaintenanceLog> findAllNextAmls(Integer pageNo, Long aircraftId) {
        return aircraftMaintenanceLogRepository.findAllNextAmls(pageNo, aircraftId);
    }

    private AmlLastPageAndAircraftInfo findAircraftInfoAndPreviousAmlPageNo(Long aircraftId) {
        AmlLastPageAndAircraftInfo amlLastPageAndAircraftInfo = aircraftService.findAircraftInfo(aircraftId);

        if (Objects.isNull(amlLastPageAndAircraftInfo)) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_AIRCRAFT, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
        AmlDropdownViewModel amlDropdownViewModel =
                aircraftMaintenanceLogRepository.findMaxAmlPageWithoutAlphabetByAircraftId(aircraftId);
        if (Objects.nonNull(amlDropdownViewModel)) {
            amlLastPageAndAircraftInfo.setPageNo(amlDropdownViewModel.getPageNo());
            amlLastPageAndAircraftInfo.setAlphabet(amlDropdownViewModel.getAlphabet());
        }

        return amlLastPageAndAircraftInfo;
    }

    private String convertTimeToString(LocalDateTime localDateTime) {
        if (Objects.nonNull(localDateTime)) {
            return String.valueOf(localDateTime.toLocalTime());
        }

        return DEFAULT_TIME_STR;
    }

    @Override
    protected AircraftMaintenanceLog convertToEntity(AircraftMaintenanceLogDto dto) {
        return null;
    }

    @Override
    protected AircraftMaintenanceLog updateEntity(AircraftMaintenanceLogDto dto, AircraftMaintenanceLog entity) {
        return null;
    }


    @Override
    public List<AmlPageViewModel> getAmlPageAndAlphabets(Long aircraftId) {
        return aircraftMaintenanceLogRepository.getAmlPageAndAlphabets(aircraftId);
    }

    @Override
    public List<DefectRectViewModel> getInterruptionInfo(Long amlId) {
        return aircraftMaintenanceLogRepository.getInterruptionInfo(amlId);
    }
}
