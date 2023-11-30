package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.AircraftMaintenanceLog;
import com.digigate.engineeringmanagement.planning.constant.OilRecordTypeEnum;
import com.digigate.engineeringmanagement.planning.payload.request.AircraftMaintenanceLogDto;
import com.digigate.engineeringmanagement.planning.payload.request.AmlOilRecordDto;
import com.digigate.engineeringmanagement.planning.entity.AmlOilRecord;
import com.digigate.engineeringmanagement.planning.payload.request.AmlRecordRequest;
import com.digigate.engineeringmanagement.planning.payload.request.OilRecordSearchDto;
import com.digigate.engineeringmanagement.planning.repository.AmlOilRecordRepository;
import com.digigate.engineeringmanagement.planning.service.AmlOilRecordIService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Aircraft cabin service
 *
 * @author Sayem Hasnat
 */
@Service
public class AmlOilRecordService extends AbstractService<AmlOilRecord, AmlOilRecordDto>
        implements AmlOilRecordIService {
    private static final Double FULL_OIL_VALUE = -1.0;

    private final IService<AircraftMaintenanceLog, AircraftMaintenanceLogDto> aircraftMaintenanceLogIService;
    private final AmlOilRecordRepository amlOilRecordRepository;

    /**
     * Autowired constructor
     *
     * @param repository                     {@link AbstractRepository}
     * @param aircraftMaintenanceLogIService {@link IService}
     * @param amlOilRecordRepository         {@link AmlOilRecordRepository}
     */
    @Autowired
    public AmlOilRecordService(
            AbstractRepository<AmlOilRecord> repository,
            IService<AircraftMaintenanceLog, AircraftMaintenanceLogDto> aircraftMaintenanceLogIService,
            AmlOilRecordRepository amlOilRecordRepository) {
        super(repository);
        this.aircraftMaintenanceLogIService = aircraftMaintenanceLogIService;
        this.amlOilRecordRepository = amlOilRecordRepository;
    }

    @Override
    protected AmlOilRecordDto convertToResponseDto(AmlOilRecord amlOilRecord) {
        AmlOilRecordDto dto = new AmlOilRecordDto();
        dto.setId(amlOilRecord.getId());
        dto.setType(amlOilRecord.getType());
        dto.setHydOil1(amlOilRecord.getHydOil1());
        dto.setHydOil2(amlOilRecord.getHydOil2());
        dto.setHydOil3(amlOilRecord.getHydOil3());
        dto.setEngineOil1(amlOilRecord.getEngineOil1());
        dto.setEngineOil2(amlOilRecord.getEngineOil2());
        dto.setApuOil(amlOilRecord.getApuOil());
        dto.setCsdOil1(amlOilRecord.getCsdOil1());
        dto.setCsdOil2(amlOilRecord.getCsdOil2());
        dto.setOilRecord(amlOilRecord.getOilRecord());
        dto.setAmlId(amlOilRecord.getAmlId());
        dto.setId(amlOilRecord.getId());
        dto.setIsActive(amlOilRecord.getIsActive());
        return dto;
    }

    @Override
    protected AmlOilRecord convertToEntity(AmlOilRecordDto amlOilRecordDto) {
        validateRecordType(amlOilRecordDto.getType());
        validateOilRecord(amlOilRecordDto, amlOilRecordDto.getAmlId());
        return convertDtoToEntity(amlOilRecordDto, new AmlOilRecord());
    }

    @Override
    public void updateActiveStatus(Long amlId, Boolean isActive) {
        List<AmlOilRecord> amlOilRecordList = amlOilRecordRepository.findByAmlId(amlId);
        for (AmlOilRecord amlOilRecord : amlOilRecordList) {
            if (Objects.equals(amlOilRecord.getIsActive(), isActive)) {
                throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
            }
            amlOilRecord.setIsActive(isActive);
        }
        try {
            amlOilRecordRepository.saveAll(amlOilRecordList);
        } catch (Exception e) {
            LOGGER.error("Exception happened while updating Oil Record status. Exception: {}", e.getMessage());
            throw new EngineeringManagementServerException(ErrorId.OIL_RECORD_ACTIVE_STATUS_CANT_BE_UPDATED,
                    HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
        }
    }

    /**
     * This method is responsible for validate record type of OilRecord
     *
     * @param typeId {@link AmlOilRecord}
     */
    private void validateRecordType(OilRecordTypeEnum typeId) {
        if (typeId.equals(OilRecordTypeEnum.OIL_RECORD_TOTAL_TYPE)) {
            throw new EngineeringManagementServerException(ErrorId.OIL_RECORD_TYPE_NAME_IS_NOT_EXIT,
                    HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
        }
    }

    /**
     * This method is responsible for validate AmlOilRecord by Type
     *
     * @param amlOilRecordDto {@link AmlOilRecordDto}
     */
    public Boolean validateOilRecord(AmlOilRecordDto amlOilRecordDto, Long amlId) {
        Optional<Long> amlOilRecordOptional =
                amlOilRecordRepository.findByTypeAndAmlId(amlOilRecordDto.getType(), amlId);
        if (amlOilRecordOptional.isEmpty()) {
            return Boolean.TRUE;
        } else {
            throw new EngineeringManagementServerException(ErrorId.RECORD_EXIST,
                    HttpStatus.BAD_REQUEST, MDC.get(ApplicationConstant.TRACE_ID));
        }
    }

    /**
     * This method is responsible for convert  AmlOilRecordDto to  AmlOilRecord
     *
     * @param amlOilRecordDto {@link AmlOilRecordDto}
     * @param amlOilRecord    {@link AmlOilRecord}
     */
    public AmlOilRecord convertDtoToEntity(AmlOilRecordDto amlOilRecordDto, AmlOilRecord amlOilRecord) {
        amlOilRecord.setType(amlOilRecordDto.getType());
        AircraftMaintenanceLog aircraftMaintenanceLog =
                aircraftMaintenanceLogIService.findById(amlOilRecordDto.getAmlId());
        amlOilRecord.setAircraftMaintenanceLog(aircraftMaintenanceLog);
        updateEntity(amlOilRecordDto, amlOilRecord);
        return amlOilRecord;
    }

    /**
     * This method responsible for find Aml Oil Record by AML ID
     *
     * @param oilRecordSearchDto {@link OilRecordSearchDto}
     */
    public List<AmlOilRecordDto> getOilRecordByAmlId(OilRecordSearchDto oilRecordSearchDto) {
        List<AmlOilRecordDto> amlOilRecordDtos = new ArrayList<>();
        AmlOilRecordDto totalAmlRecord;
        List<AmlOilRecord> amlOilRecordList = amlOilRecordRepository.
                findByAmlIdAndIsActive(oilRecordSearchDto.getAmlId(),
                        oilRecordSearchDto.getIsActive());
        if (CollectionUtils.isEmpty(amlOilRecordList)) {
            return Collections.emptyList();
        }
        amlOilRecordDtos.add(convertToResponseDto(amlOilRecordList.get(0)));
        totalAmlRecord = convertToResponseDto(amlOilRecordList.get(0));
        totalAmlRecord.setId(null);
        totalAmlRecord.setIsActive(null);
        totalAmlRecord.setType(OilRecordTypeEnum.OIL_RECORD_TOTAL_TYPE);


        if (amlOilRecordList.size() == 2) {
            AmlOilRecord amlOilRecord = amlOilRecordList.get(1);
            amlOilRecordDtos.add(convertToResponseDto(amlOilRecord));
            totalAmlRecord.setHydOil1(calculateSum(totalAmlRecord.getHydOil1(), amlOilRecord.getHydOil1()));
            totalAmlRecord.setHydOil2(calculateSum(totalAmlRecord.getHydOil2(), amlOilRecord.getHydOil2()));
            totalAmlRecord.setHydOil3(calculateSum(totalAmlRecord.getHydOil3(), amlOilRecord.getHydOil3()));
            totalAmlRecord.setEngineOil1(calculateSum(totalAmlRecord.getEngineOil1(), amlOilRecord.getEngineOil1()));
            totalAmlRecord.setEngineOil2(calculateSum(totalAmlRecord.getEngineOil2(), amlOilRecord.getEngineOil2()));
            totalAmlRecord.setApuOil(calculateSum(totalAmlRecord.getApuOil(), amlOilRecord.getApuOil()));
            totalAmlRecord.setCsdOil1(calculateSum(totalAmlRecord.getCsdOil1(), amlOilRecord.getCsdOil1()));
            totalAmlRecord.setCsdOil2(calculateSum(totalAmlRecord.getCsdOil2(), amlOilRecord.getCsdOil2()));
            totalAmlRecord.setOilRecord(calculateSum(totalAmlRecord.getOilRecord(), amlOilRecord.getOilRecord()));
        }
        amlOilRecordDtos.add(totalAmlRecord);
        return amlOilRecordDtos;
    }

    private Double calculateSum(Double firstValue, Double secondValue) {
        if (Objects.equals(firstValue, FULL_OIL_VALUE) || Objects.equals(secondValue, FULL_OIL_VALUE)) {
            return FULL_OIL_VALUE;
        }
        return firstValue + secondValue;
    }

    /**
     * This method is responsible for save a batch of Oil Record
     *
     * @param amlRecordRequest {@link AmlRecordRequest}
     * @param amlId            {@link Long}
     */
    public void saveAllRecords(AmlRecordRequest amlRecordRequest, Long amlId) {
        validateAmlOilRecord(amlRecordRequest, amlId);
        saveItemList(List.of(convertDtoToEntity(amlRecordRequest.getOnArrival(), new AmlOilRecord()),
                convertDtoToEntity(amlRecordRequest.getUpLift(), new AmlOilRecord())));
    }

    private void validateAmlOilRecord(AmlRecordRequest amlRecordRequest, Long amlId) {
        validateTypeAndAmlId(amlRecordRequest, amlId);
        validateClientData(amlRecordRequest.getOnArrival(), amlId);
        validateClientData(amlRecordRequest.getUpLift(), amlId);
    }

    private void validateTypeAndAmlId(AmlRecordRequest amlRecordRequest, Long amlId) {
        if (amlRecordRequest.getOnArrival().getType().equals(amlRecordRequest.getUpLift().getType())) {
            throw  EngineeringManagementServerException.badRequest(ErrorId.AML_RECORD_TYPE_CAN_NOT_BE_SAME);
        }
        if (!amlId.equals(amlRecordRequest.getOnArrival().getAmlId())
                || !amlId.equals(amlRecordRequest.getUpLift().getAmlId())) {
            throw  EngineeringManagementServerException.badRequest(ErrorId.AML_ID_MISS_MATCH);
        }
    }

    /**
     * This method is responsible for update a batch of Oil Record
     *
     * @param amlRecordRequest {@link AmlRecordRequest}
     */
    public void updateAllRecords(AmlRecordRequest amlRecordRequest, Long amlId) {
        validateTypeAndAmlId(amlRecordRequest, amlId);
        Map<Long, AmlOilRecordDto> amlOilRecordDtoMap = new HashMap<>();
        amlOilRecordDtoMap.put(amlRecordRequest.getOnArrival().getId(),
                amlRecordRequest.getOnArrival());
        amlOilRecordDtoMap.put(amlRecordRequest.getUpLift().getId(),
                amlRecordRequest.getUpLift());
        List<AmlOilRecord> amlOilRecordList = getAllByDomainIdIn(
                Set.of(amlRecordRequest.getOnArrival().getId(),
                        amlRecordRequest.getUpLift().getId()), true);
        if (CollectionUtils.isEmpty(amlOilRecordList)) {
            throw EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND);
        }
        if (amlOilRecordList.size() != 2) {
            throw EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND);
        }
        amlOilRecordList.stream().forEach(amlOilRecord ->
                updateEntity(amlOilRecordDtoMap.get(amlOilRecord.getId()), amlOilRecord));
    }

    /**
     * This is helper method which use to update batch oil record
     * to convert dto of record update
     *
     * @param amlOilRecordDto {@link AmlOilRecordDto}
     * @param amlOilRecord    {@link AmlOilRecord }
     */
    @Override
    public AmlOilRecord updateEntity(AmlOilRecordDto amlOilRecordDto, AmlOilRecord amlOilRecord) {
        amlOilRecord.setHydOil1(amlOilRecordDto.getHydOil1());
        amlOilRecord.setHydOil2(amlOilRecordDto.getHydOil2());
        amlOilRecord.setHydOil3(amlOilRecordDto.getHydOil3());
        amlOilRecord.setEngineOil1(amlOilRecordDto.getEngineOil1());
        amlOilRecord.setEngineOil2(amlOilRecordDto.getEngineOil2());
        amlOilRecord.setApuOil(amlOilRecordDto.getApuOil());
        amlOilRecord.setCsdOil1(amlOilRecordDto.getCsdOil1());
        amlOilRecord.setCsdOil2(amlOilRecordDto.getCsdOil2());
        amlOilRecord.setOilRecord(amlOilRecordDto.getOilRecord());
        return amlOilRecord;
    }

    /**
     *This method is responsible for get all oil uplift records by aml id
     *
     * @param amlIds  {@link Set<Long>}
     * @return amlOilRecord        {@link AmlOilRecord}
     */
    @Override
    public List<AmlOilRecord> findAllByAmlIdInAndType(Set<Long> amlIds) {
        return this.amlOilRecordRepository.findAllByAmlIdInAndType(amlIds, OilRecordTypeEnum.UPLIFT);
    }

    @Override
    public List<AmlOilRecord> findAllByAmlIdInAndTypeOnArrival(Set<Long> amlIds) {
        return this.amlOilRecordRepository.findAllByAmlIdInAndType(amlIds, OilRecordTypeEnum.ON_ARRIVAL);
    }
}
