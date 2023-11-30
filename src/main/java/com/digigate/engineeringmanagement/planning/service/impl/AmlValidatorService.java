package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.erpDataSync.Employee;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.erpDataSync.EmployeeService;
import com.digigate.engineeringmanagement.common.util.NumberUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftIService;
import com.digigate.engineeringmanagement.planning.constant.AmlType;
import com.digigate.engineeringmanagement.planning.entity.AircraftMaintenanceLog;
import com.digigate.engineeringmanagement.planning.entity.AircraftMaintenanceLogSignature;
import com.digigate.engineeringmanagement.planning.entity.Airport;
import com.digigate.engineeringmanagement.planning.entity.Signature;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftMaintenanceLogDto;
import com.digigate.engineeringmanagement.planning.payload.request.AmlFlightDataDto;
import com.digigate.engineeringmanagement.planning.payload.request.MaintenanceLogSignatureDto;
import com.digigate.engineeringmanagement.planning.payload.request.PageNoDto;
import com.digigate.engineeringmanagement.planning.payload.response.AmlDropdownViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.AmlLastPageAndAircraftInfo;
import com.digigate.engineeringmanagement.planning.repository.AircraftMaintenanceLogRepository;
import com.digigate.engineeringmanagement.planning.service.AirportService;
import com.digigate.engineeringmanagement.planning.service.AmlBookService;
import com.digigate.engineeringmanagement.planning.service.SignatureService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AmlValidatorService {

    private final AircraftMaintenanceLogRepository amlRepository;

    private final AirportService airportService;
    private final AircraftIService aircraftIService;
    private final EmployeeService employeeService;
    private final AmlBookService amlBookService;
    private final SignatureService signatureService;

    private static final String PAGE_NO = "pageNo";
    private static final String ALPHABET = "alphabet";

    private static final int DEFAULT_INVALID_PAGE_NO = -1;

    private static final String CREATED_AT = "createdAt";

    public AmlValidatorService(AircraftMaintenanceLogRepository amlRepository,
                               AirportService airportService, AircraftIService aircraftIService,
                               EmployeeService employeeService, AmlBookService amlBookService,
                               SignatureService signatureService) {
        this.amlRepository = amlRepository;
        this.airportService = airportService;
        this.aircraftIService = aircraftIService;
        this.employeeService = employeeService;
        this.amlBookService = amlBookService;
        this.signatureService = signatureService;
    }


    protected AircraftMaintenanceLog convertToEntity(AircraftMaintenanceLogDto aircraftMaintenanceLogDto) {
        return convertToEntity(aircraftMaintenanceLogDto, new AircraftMaintenanceLog(), false);
    }


    protected AircraftMaintenanceLog updateEntity(AircraftMaintenanceLogDto aircraftMaintenanceLogDto,
                                                  AircraftMaintenanceLog entity) {
        return convertToEntity(aircraftMaintenanceLogDto, entity, true);
    }

    private AircraftMaintenanceLog convertToEntity(AircraftMaintenanceLogDto aircraftMaintenanceLogDto,
                                                   AircraftMaintenanceLog aircraftMaintenanceLog, Boolean isUpdatable) {
        if (!isUpdatable) {
            validatePageNo(aircraftMaintenanceLogDto);
            aircraftMaintenanceLog.setPageNo(aircraftMaintenanceLogDto.getPageNo());
            aircraftMaintenanceLog.setAlphabet(aircraftMaintenanceLogDto.getAlphabet());
        }

        validateAmlDate(aircraftMaintenanceLogDto);

        if (Objects.nonNull(aircraftMaintenanceLogDto.getAircraftId())) {
            Aircraft aircraft =
                    aircraftIService.findById(aircraftMaintenanceLogDto.getAircraftId());
            aircraftMaintenanceLog.setAircraft(aircraft);
            aircraftMaintenanceLog.setAmlAircraftId(aircraftMaintenanceLogDto.getAircraftId());
        }

        if (Objects.nonNull(aircraftMaintenanceLogDto.getFromAirportId())) {
            Airport airport = airportService.findActiveAirportById(aircraftMaintenanceLogDto.getFromAirportId());
            aircraftMaintenanceLog.setFromAirport(airport);
            aircraftMaintenanceLog.setFromAirportId(aircraftMaintenanceLogDto.getFromAirportId());
        } else {
            if (Objects.nonNull(aircraftMaintenanceLog.getFromAirportId())) {
                aircraftMaintenanceLog.setFromAirport(null);
            }
        }

        if (Objects.nonNull(aircraftMaintenanceLogDto.getToAirportId())) {
            Airport airport = airportService.findActiveAirportById(aircraftMaintenanceLogDto.getToAirportId());
            aircraftMaintenanceLog.setToAirport(airport);
            aircraftMaintenanceLog.setToAirportId(aircraftMaintenanceLogDto.getToAirportId());
        } else {
            if (Objects.nonNull(aircraftMaintenanceLog.getToAirportId())) {
                aircraftMaintenanceLog.setToAirport(null);
            }
        }

        if (Objects.nonNull(aircraftMaintenanceLogDto.getPreFlightInspectionAirportId())) {
            Airport airport =
                    airportService.findActiveAirportById(aircraftMaintenanceLogDto.getPreFlightInspectionAirportId());
            aircraftMaintenanceLog.setPreFlightInspectionAirport(airport);
            aircraftMaintenanceLog
                    .setPreFlightInspectionAirportId(aircraftMaintenanceLogDto.getPreFlightInspectionAirportId());
        }

        if (Objects.nonNull(aircraftMaintenanceLogDto.getCaptainId())) {
            Employee employee = employeeService.findById(aircraftMaintenanceLogDto.getCaptainId());
            aircraftMaintenanceLog.setCaptain(employee);
            aircraftMaintenanceLog.setCaptainId(aircraftMaintenanceLogDto.getCaptainId());
        }

        if (Objects.nonNull(aircraftMaintenanceLogDto.getFirstOfficerId())) {
            Employee employee = employeeService.findById(aircraftMaintenanceLogDto.getFirstOfficerId());
            aircraftMaintenanceLog.setFirstOfficer(employee);
            aircraftMaintenanceLog.setFirstOfficerId(aircraftMaintenanceLogDto.getFirstOfficerId());
        }

        buildRelationWithSignature(aircraftMaintenanceLogDto, aircraftMaintenanceLog, isUpdatable);

        aircraftMaintenanceLog.setPfiTime(aircraftMaintenanceLogDto.getPfiTime());
        aircraftMaintenanceLog.setOcaTime(aircraftMaintenanceLogDto.getOcaTime());
        aircraftMaintenanceLog.setAlphabet(aircraftMaintenanceLogDto.getAlphabet());
        aircraftMaintenanceLog.setFlightNo(aircraftMaintenanceLogDto.getFlightNo());
        aircraftMaintenanceLog.setDate(aircraftMaintenanceLogDto.getDate());
        aircraftMaintenanceLog.setRefuelDelivery(aircraftMaintenanceLogDto.getRefuelDelivery());
        aircraftMaintenanceLog.setSpecificGravity(aircraftMaintenanceLogDto.getSpecificGravity());
        aircraftMaintenanceLog.setConvertedIn(aircraftMaintenanceLogDto.getConvertedIn());
        aircraftMaintenanceLog.setRemarks(aircraftMaintenanceLogDto.getRemarks());
        aircraftMaintenanceLog.setAmlType(aircraftMaintenanceLogDto.getAmlType());

        return aircraftMaintenanceLog;
    }

    private void validateAmlDate(AircraftMaintenanceLogDto aircraftMaintenanceLogDto) {
        if (Objects.nonNull(aircraftMaintenanceLogDto.getDate())) {
            if (Objects.isNull(aircraftMaintenanceLogDto.getAlphabet())) {
                Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, CREATED_AT));
                Page<LocalDate> previousDatePage =
                        amlRepository.findPreviousAmlDate(aircraftMaintenanceLogDto.getAircraftId(),
                                aircraftMaintenanceLogDto.getPageNo(), pageable);

                pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, PAGE_NO)
                        .and(Sort.by(Sort.Direction.ASC, ALPHABET)));

                Page<LocalDate> nextDatePage =
                        amlRepository.findNextAmlDate(aircraftMaintenanceLogDto.getAircraftId(),
                                aircraftMaintenanceLogDto.getPageNo(), pageable);

                List<LocalDate> previousDateList = previousDatePage.getContent();
                List<LocalDate> nextDateList = nextDatePage.getContent();

                if (CollectionUtils.isNotEmpty(previousDateList) && CollectionUtils.isNotEmpty(nextDateList)) {
                    LocalDate previousDate = previousDateList.get(0);
                    LocalDate nextDate = nextDateList.get(0);
                    LocalDate atlDate = aircraftMaintenanceLogDto.getDate();
                    if (Objects.nonNull(previousDate) && Objects.nonNull(nextDate)
                            && (previousDate.isAfter(atlDate) || nextDate.isBefore(atlDate))) {
                        throw new EngineeringManagementServerException(
                                ErrorId.INVALID_ATL_DATE, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
                        );
                    }
                } else if (CollectionUtils.isNotEmpty(previousDateList) && CollectionUtils.isEmpty(nextDateList)) {
                    LocalDate previousDate = previousDateList.get(0);
                    if (Objects.nonNull(previousDate) && previousDate.isAfter(aircraftMaintenanceLogDto.getDate())) {
                        throw new EngineeringManagementServerException(
                                ErrorId.INVALID_AML_DATE, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
                        );
                    }
                }
            } else {
                Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, PAGE_NO)
                        .and(Sort.by(Sort.Direction.DESC, ALPHABET)));
                Page<LocalDate> previousDatePage =
                        amlRepository.findPreviousAmlDate(aircraftMaintenanceLogDto.getAircraftId(),
                                aircraftMaintenanceLogDto.getPageNo(), aircraftMaintenanceLogDto.getAlphabet(),
                                pageable);
                LocalDate previousDate = CollectionUtils.isNotEmpty(previousDatePage.getContent()) ?
                        previousDatePage.getContent().get(0) : null;

                pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, PAGE_NO)
                        .and(Sort.by(Sort.Direction.ASC, ALPHABET)));

                Page<LocalDate> nextAmlDatePage =
                        amlRepository.findNextAmlDate(aircraftMaintenanceLogDto.getAircraftId(),
                                aircraftMaintenanceLogDto.getPageNo(), aircraftMaintenanceLogDto.getAlphabet(),
                                pageable);

                LocalDate nextAmlDate = CollectionUtils.isNotEmpty(nextAmlDatePage.getContent()) ?
                        nextAmlDatePage.getContent().get(0) : null;

                if (Objects.nonNull(previousDate) && Objects.nonNull(nextAmlDate)
                        && (aircraftMaintenanceLogDto.getDate().isBefore(previousDate)
                        || aircraftMaintenanceLogDto.getDate().isAfter(nextAmlDate))) {

                    throw new EngineeringManagementServerException(
                            ErrorId.INVALID_ATL_DATE, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
                    );
                } else if (Objects.nonNull(previousDate) && Objects.isNull(nextAmlDate)
                        && (aircraftMaintenanceLogDto.getDate().isBefore(previousDate))) {
                    throw new EngineeringManagementServerException(
                            ErrorId.INVALID_AML_DATE, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
                    );
                }
            }
        }
    }

    private void validatePageNo(AircraftMaintenanceLogDto aircraftMaintenanceLogDto) {
        PageNoDto pageNoDto = PageNoDto.builder()
                .aircraftId(aircraftMaintenanceLogDto.getAircraftId())
                .pageNo(aircraftMaintenanceLogDto.getPageNo())
                .alphabet(aircraftMaintenanceLogDto.getAlphabet())
                .build();
        this.validateAmlPageNo(pageNoDto);
    }

    public void validateAmlPageNo(PageNoDto pageNoDto) {
        if (Objects.nonNull(pageNoDto.getAlphabet()) &&
                !(pageNoDto.getAlphabet() >= 'A' && pageNoDto.getAlphabet() <= 'Z')) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_ALPHABET, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        List<AmlLastPageAndAircraftInfo> pages =
                amlRepository.findAllAmlByPageNo(pageNoDto.getPageNo(), pageNoDto.getAircraftId());

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
                amlRepository.findAllAmlIdsByPageNoAndAircraftAndAlphabet(pageNoDto.getPageNo(),
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
                    amlRepository.findAllByAircraftIdAndPageNo(pageNoDto.getAircraftId(), pageNo);

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

    private AmlLastPageAndAircraftInfo findAircraftInfoAndPreviousAmlPageNo(Long aircraftId) {
        AmlLastPageAndAircraftInfo amlLastPageAndAircraftInfo = aircraftIService.findAircraftInfo(aircraftId);

        if (Objects.isNull(amlLastPageAndAircraftInfo)) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_AIRCRAFT, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
        AmlDropdownViewModel amlDropdownViewModel =
                amlRepository.findMaxAmlPageWithoutAlphabetByAircraftId(aircraftId);
        if (Objects.nonNull(amlDropdownViewModel)) {
            amlLastPageAndAircraftInfo.setPageNo(amlDropdownViewModel.getPageNo());
            amlLastPageAndAircraftInfo.setAlphabet(amlDropdownViewModel.getAlphabet());
        }

        return amlLastPageAndAircraftInfo;
    }

    private void buildRelationWithSignature(AircraftMaintenanceLogDto aircraftMaintenanceLogDto,
                                            AircraftMaintenanceLog aircraftMaintenanceLog, Boolean isUpdatable) {

        Set<Long> signatureIds = new HashSet<>();
        aircraftMaintenanceLogDto.getMaintenanceLogSignatureDtoList().forEach(d -> {
            if (Objects.nonNull(d) && Objects.nonNull(d.getSignatureId())) {
                signatureIds.add(d.getSignatureId());
            }
        });

        List<Signature> signatureList = signatureService.getAllByDomainIdIn(signatureIds, true);

        Map<Long, Signature> signatureMap =
                signatureList.stream().collect(Collectors.toMap(Signature::getId, signature -> signature));

        if (isUpdatable) {
            if (CollectionUtils.isEmpty(aircraftMaintenanceLogDto.getMaintenanceLogSignatureDtoList())) {
                if (CollectionUtils.isNotEmpty(aircraftMaintenanceLog.getAircraftMaintenanceLogSignatures())) {
                    aircraftMaintenanceLog.getAircraftMaintenanceLogSignatures().clear();
                }
                return;
            }
        }

        Map<Long, AircraftMaintenanceLogSignature> maintenanceLogSignatureMap = new HashMap<>();

        if (Objects.nonNull(aircraftMaintenanceLog.getAircraftMaintenanceLogSignatures())) {
            maintenanceLogSignatureMap = aircraftMaintenanceLog.getAircraftMaintenanceLogSignatures().stream()
                    .collect(Collectors.toMap(AircraftMaintenanceLogSignature::getId,
                            amlSignature -> amlSignature));
        }

        Set<Long> amlSignatureIds = new HashSet<>();

        for (MaintenanceLogSignatureDto maintenanceLogSignatureDto :
                aircraftMaintenanceLogDto.getMaintenanceLogSignatureDtoList()) {
            if (Objects.nonNull(maintenanceLogSignatureDto) && Objects.nonNull(maintenanceLogSignatureDto.getAmlSignatureId())) {
                AircraftMaintenanceLogSignature aircraftMaintenanceLogSignature =
                        maintenanceLogSignatureMap.get(maintenanceLogSignatureDto.getAmlSignatureId());
                if (Objects.isNull(aircraftMaintenanceLogSignature)) {
                    throw EngineeringManagementServerException.badRequest(ErrorId.DATA_NOT_FOUND);
                }
                aircraftMaintenanceLogSignature.setSignature(
                        signatureMap.get(maintenanceLogSignatureDto.getSignatureId()));
                aircraftMaintenanceLogSignature.setSignatureType(maintenanceLogSignatureDto.getSignatureType());
                aircraftMaintenanceLog
                        .addAircraftMaintenanceLogSignature(aircraftMaintenanceLogSignature);
                amlSignatureIds.add(maintenanceLogSignatureDto.getAmlSignatureId());
            }
        }

        Map<Long, AircraftMaintenanceLogSignature> finalMaintenanceLogSignatureMap = maintenanceLogSignatureMap;
        if (isUpdatable) {
            aircraftMaintenanceLog.getAircraftMaintenanceLogSignatures()
                    .forEach(
                            aircraftMaintenanceLogSignature -> {
                                if (Objects.nonNull(aircraftMaintenanceLogSignature.getId())
                                        && !amlSignatureIds.contains(aircraftMaintenanceLogSignature.getId())) {
                                    aircraftMaintenanceLog.getAircraftMaintenanceLogSignatures()
                                            .remove(finalMaintenanceLogSignatureMap
                                                    .get(aircraftMaintenanceLogSignature.getId()));
                                }
                            }
                    );
        }
    }

    public void validateMaintenanceLogData(AircraftMaintenanceLogDto aircraftMaintenanceLogDto) {
        AmlFlightDataDto amlFlightDataDto = aircraftMaintenanceLogDto.getAmlFlightData();
        if (Objects.isNull(amlFlightDataDto) || Objects.isNull(amlFlightDataDto.getTotalAirTime())
                || Objects.isNull(amlFlightDataDto.getTotalLanding())) {
            throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_FLIGHT_DATA_DTO);
        }

        if (aircraftMaintenanceLogDto.getAmlType().equals(AmlType.VOID)
                || aircraftMaintenanceLogDto.getAmlType().equals(AmlType.NIL)) {
            if (Objects.nonNull(aircraftMaintenanceLogDto.getAlphabet())) {
                throw EngineeringManagementServerException.badRequest(ErrorId.ALPHABET_NOT_REQUIRED_FOR_VOID_OR_NIL_TYPE);
            }
        }

        if (aircraftMaintenanceLogDto.getAmlType().equals(AmlType.REGULAR)) {
            if (Objects.isNull(amlFlightDataDto.getBlockOffTime())
                    || Objects.isNull(amlFlightDataDto.getBlockOnTime())
                    || Objects.isNull(amlFlightDataDto.getTakeOffTime())
                    || Objects.isNull(amlFlightDataDto.getLandingTime())
                    || Objects.isNull(amlFlightDataDto.getNoOfLanding())) {
                throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_FLIGHT_DATA_DTO);
            }
            validateFightDataDto(amlFlightDataDto, aircraftMaintenanceLogDto.getDate());

        } else {

            if (aircraftMaintenanceLogDto.getAmlType().equals(AmlType.MAINT)) {
                if (aircraftMaintenanceLogDto.getNeedToSaveDefectRectification()) {
                    if (CollectionUtils.isEmpty(aircraftMaintenanceLogDto.getDefectRectifications())) {
                        throw new EngineeringManagementServerException(
                                ErrorId.INVALID_DEFECT_AND_RECTIFICATION, HttpStatus.BAD_REQUEST,
                                MDC.get(ApplicationConstant.TRACE_ID)
                        );
                    }
                }
            }
        }

    }

    private void validateFightDataDto(AmlFlightDataDto amlFlightDataDto, LocalDate amlDate) {

        LocalDate blockOffDate = amlFlightDataDto.getBlockOffTime().toLocalDate();
        LocalDate blockOnDate = amlFlightDataDto.getBlockOnTime().toLocalDate();

        if (blockOffDate.isAfter(amlDate) || (blockOffDate.isBefore(amlDate) &&
                !blockOffDate.equals(amlDate.minusDays(1)))) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_BLOCK_OFF_DATE, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (blockOnDate.isBefore(amlDate) || (blockOnDate.isAfter(amlDate)) &&
                !blockOnDate.equals(amlDate.plusDays(1))) {
            throw new EngineeringManagementServerException(
                    ErrorId.INVALID_BLOCK_ON_DATE, HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (amlFlightDataDto.getBlockOffTime().isAfter(amlFlightDataDto.getBlockOnTime())) {
            throw new EngineeringManagementServerException(
                    ErrorId.BLOCK_OFF_TIME_MUST_BE_BEFORE_BLOCK_ON_TIME, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (amlFlightDataDto.getTakeOffTime().isAfter(amlFlightDataDto.getLandingTime())) {
            throw new EngineeringManagementServerException(
                    ErrorId.TAKE_OFF_MUST_BE_BEFORE_LANDING_TIME_TIME, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (amlFlightDataDto.getBlockOffTime().isAfter(amlFlightDataDto.getTakeOffTime())) {
            throw new EngineeringManagementServerException(
                    ErrorId.BLOCK_OFF_TIME_MUST_BE_BEFORE_TAKE_OFF_TIME, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        if (amlFlightDataDto.getLandingTime().isAfter(amlFlightDataDto.getBlockOnTime())) {
            throw new EngineeringManagementServerException(
                    ErrorId.LANDING_TIME_MUST_BE_BEFORE_BLOCK_ON_TIME, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID)
            );
        }
    }
}
