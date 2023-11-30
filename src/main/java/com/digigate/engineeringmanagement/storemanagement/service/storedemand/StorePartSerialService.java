package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.planning.constant.PartStatus;
import com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType;
import com.digigate.engineeringmanagement.planning.dto.request.SerialRequestDto;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.entity.Serial;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.planning.service.SerialService;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartAvailability;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartSerialGrnProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StorePartSerialProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.PartSerialSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartSerialInternalDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartSerialRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.UnserviceableComponentListViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.StorePartSerialResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.StorePartSerialRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.CurrencyService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.UnitMeasurementService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType.ISSUE;
import static com.digigate.engineeringmanagement.planning.constant.StorePartAvailabilityLogParentType.SCRAP;

@Service
public class StorePartSerialService extends AbstractSearchService<StorePartSerial,
        StorePartSerialRequestDto, PartSerialSearchDto> {

    private final StorePartSerialRepository storePartSerialRepository;

    private final StorePartAvailabilityService storePartAvailabilityService;

    private final SerialService serialService;
    private final PartService partService;
    private final CurrencyService currencyService;
    private final UnitMeasurementService unitMeasurementService;


    @Autowired
    public StorePartSerialService(StorePartSerialRepository storePartSerialRepository,
                                  StorePartAvailabilityService storePartAvailabilityService,
                                  SerialService serialService,
                                  PartService partService,
                                  CurrencyService currencyService, UnitMeasurementService unitMeasurementService) {

        super(storePartSerialRepository);
        this.storePartSerialRepository = storePartSerialRepository;
        this.storePartAvailabilityService = storePartAvailabilityService;
        this.serialService = serialService;
        this.partService = partService;
        this.currencyService = currencyService;
        this.unitMeasurementService = unitMeasurementService;
    }

    public Pair<StorePartSerial, Serial> createNewSerial(Long partId, String serialNo, StorePartSerialRequestDto partSerialRequestDto) {
        StorePartAvailability availability = storePartAvailabilityService.findByPartId(partId);
        Optional<Serial> serialEntity = serialService.findSerialByPartIdAndSerialNo(partId,serialNo);
        if(serialEntity.isPresent()){
            StorePartSerial partSerial = populateEntity(partSerialRequestDto, new StorePartSerial(), availability, serialEntity.get());
            partSerial.setIsActive(false);
            return Pair.of(super.saveItem(partSerial), serialEntity.get());
        }else{
            Serial serial = serialService.create(new SerialRequestDto(partId, serialNo));
            StorePartSerial partSerial = populateEntity(partSerialRequestDto, new StorePartSerial(), availability, serial);
            partSerial.setIsActive(false);
            return Pair.of(super.saveItem(partSerial), serial);
        }
    }

    public void deleteSerial(Long partId, String serialNo) {
        StorePartAvailability availability = storePartAvailabilityService.findByPartId(partId);

        Serial serial = serialService.findSerialByPartIdAndSerialNo(partId, serialNo).orElseThrow(() ->
                EngineeringManagementServerException.badRequest(Helper.createDynamicCode(ErrorId.DATA_NOT_FOUND_DYNAMIC, SERIAL_TABLE)));

        storePartSerialRepository.deleteBySerialIdAndStorePartAvailabilityId(serial.getId(), availability.getId());
        serialService.delete(serial);
    }

    public List<StorePartSerial> findAllByIdIn(Set<Long> ids) {
        return storePartSerialRepository.findAllByIdIn(ids);
    }

    public List<PartSerialGrnProjection> findGrnByPartSerialByIdIn(Set<Long> ids) {
        return storePartSerialRepository.findDataByIdIn(ids);
    }

    public List<StorePartSerialProjection> findStorePartSerialByIdIn(Set<Long> ids) {
        return storePartSerialRepository.findStorePartSerialByIdIn(ids);
    }

    @Transactional
    public StorePartSerial create(StorePartSerialRequestDto storePartSerialRequestDto) {
        StorePartSerial storePartSerial = convertToEntity(storePartSerialRequestDto);
        return super.saveItem(storePartSerial);
    }

    @Transactional
    public StorePartSerial findAndUpdateStorePartSerial(StorePartSerialInternalDto storePartSerialInternalDto) {
        StorePartSerial storePartSerial;
        boolean needToUpdateQuantity = true;
        Part part = storePartSerialInternalDto.getPart() == null ?
            partService.findByPartNo(storePartSerialInternalDto.getPartNo()) : storePartSerialInternalDto.getPart();
        StorePartAvailability storePartAvailability = storePartAvailabilityService.findByPartIdUnfiltered(part.getId())
            .orElseGet(() -> storePartAvailabilityService.saveItem(StorePartAvailability.from(part)));

        Optional<Serial> serialOptional = serialService.findSerialByPartIdAndSerialNo(part.getId(), storePartSerialInternalDto.getSerialNo());
        Serial serial = serialOptional.orElseGet(() -> serialService.saveItem(Serial.builder().part(part).serialNumber(storePartSerialInternalDto.getSerialNo()).build()));
        Optional<StorePartSerial> partSerialOptional = storePartSerialRepository.findByStorePartAvailabilityIdAndSerialId(
            storePartAvailability.getId(), serial.getId());
        if (partSerialOptional.isPresent()) {
            storePartSerial = partSerialOptional.get();
            if (storePartSerial.getParentType() == storePartSerialInternalDto.getParentType()) { //TODO: confirm requirement if need to throw error
                needToUpdateQuantity = false;
            }
        } else {
            storePartSerial = new StorePartSerial();
        }

        populateStorePartSerial(storePartSerialInternalDto, storePartAvailability, serial, storePartSerial);

        if (needToUpdateQuantity) {
            storePartAvailabilityService.updatePartQuantity(storePartAvailability, storePartSerialInternalDto.getTransactionType(),
                storePartSerialInternalDto.getQuantity());
        }
        return saveItem(storePartSerial);
    }

    @Transactional
    public StorePartSerial update(StorePartSerialRequestDto storePartSerialRequestDto, Long id) {
        StorePartSerial storePartSerial = findByIdUnfiltered(id);
        final StorePartSerial entity = updateEntity(storePartSerialRequestDto, storePartSerial);
        return super.saveItem(entity);
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        StorePartSerial storePartSerial = findByIdUnfiltered(id);
        super.updateActiveStatus(id, isActive);
        storePartSerial.setIsActive(isActive);
        saveItem(storePartSerial);
    }

    public Set<StorePartSerial> getStorePartSerialNos(Set<Long> ids, Long storePartsAvailabilityId, PartStatus partStatus) {
        return storePartSerialRepository.findAllByIdInAndStorePartAvailabilityIdAndPartStatusAndIsActiveTrue(ids,
                storePartsAvailabilityId, partStatus);
    }

    @Override
    protected StorePartSerialResponseDto convertToResponseDto(StorePartSerial storePartSerial) {
        UnitMeasurement unitMeasurement = storePartSerial.getUnitMeasurement();
        if(Objects.isNull(unitMeasurement)){
            unitMeasurement = new UnitMeasurement();
        }

        return StorePartSerialResponseDto.builder()
                .id(storePartSerial.getId())
                .availId(storePartSerial.getStorePartAvailabilityId())
                .grnNo(storePartSerial.getGrnNo())
                .quantity(storePartSerial.getQuantity())
                .parentType(storePartSerial.getParentType())
                .partStatus(storePartSerial.getPartStatus())
                .serialId(storePartSerial.getSerialId())
                .serialNo(storePartSerial.getSerialNumber())
                .currencyId(storePartSerial.getCurrencyId())
                .currencyCode(storePartSerial.getCurrencyCode())
                .uomId(unitMeasurement.getId())
                .uomCode(unitMeasurement.getCode())
                .price(storePartSerial.getPrice())
                .shelfLifeType(storePartSerial.getShelfLifeType())
                .rackLife(storePartSerial.getRackLife())
                .selfLife(storePartSerial.getSelfLife())
                .issued(Objects.equals(storePartSerial.getParentType(), StorePartAvailabilityLogParentType.ISSUE))
                .build();
    }

    @Override
    protected StorePartSerial convertToEntity(StorePartSerialRequestDto dto) {
        return populateToEntity(dto, new StorePartSerial(), true);
    }

    @Override
    protected StorePartSerial updateEntity(StorePartSerialRequestDto dto, StorePartSerial entity) {
        return populateToEntity(dto, entity, false);
    }

    @Override
    protected Specification<StorePartSerial> buildSpecification(PartSerialSearchDto searchDto) {

        CustomSpecification<StorePartSerial> customSpecification = new CustomSpecification<>();
        if (searchDto.getPartId() == null) {
            return Specification.where(customSpecification.active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD)
                    .and(customSpecification.likeSpecificationAtChild(searchDto.getQuery(), SERIAL_TABLE, SERIAL_NUMBER))
                    .and(customSpecification.equalSpecificationAtRoot(searchDto.getAvailId(), PART_AVAILABILITY_ID)));

        } else {

            Specification<StorePartSerial> storePartSerialCustomSpecification = Specification.where(
                    customSpecification.likeSpecificationAtChild(searchDto.getQuery(), SERIAL_TABLE, SERIAL_NUMBER)
                            .and(customSpecification.equalSpecificationAtChild(searchDto.getPartId(),
                                    ApplicationConstant.PART_AVAILABILITY, ApplicationConstant.PART_ID))
                            .and(customSpecification.equalSpecificationAtRoot(searchDto.getUomId(),ApplicationConstant.UNIT_MEASURE_ID))
                            .and(customSpecification.equalSpecificationAtRoot(searchDto.getStatus(), ApplicationConstant.PART_STATUS)));
            if (Objects.nonNull(searchDto.getOnlyAvailable())) {
                storePartSerialCustomSpecification = storePartSerialCustomSpecification.and(customSpecification.notEqualSpecificationAtRoot(ISSUE, ApplicationConstant.PARENT_TYPE))
                        .and(customSpecification.notEqualSpecificationAtRoot(SCRAP, ApplicationConstant.PARENT_TYPE));
            }
            return storePartSerialCustomSpecification;
        }
    }

    public StorePartSerial findOrCreateStoreSerial(Long partSerialId, Long uomId, Long partId, Long quantityReturn) {
        return findValidStoreSerial(partSerialId, partId)
                .orElseGet(() -> createNewStorePartSerial(partId, partSerialId, quantityReturn, uomId));
    }

    private Optional<StorePartSerial> findValidStoreSerial(Long partSerialId, Long partId) {
        Optional<StorePartSerial> serialOptional = storePartSerialRepository.findBySerialIdAndStorePartAvailabilityPartIdAndIsActiveTrue(partSerialId, partId);
        if (serialOptional.isPresent()) {
            StorePartSerial partSerial = serialOptional.get();
            if (partSerial.getParentType() == SCRAP) {
                throw EngineeringManagementServerException.notFound(ErrorId.SERIAL_ISSUED_OR_SCRAPPED);
            } else if (partSerial.getPartStatus() == PartStatus.UNSERVICEABLE) {
                throw EngineeringManagementServerException.dataSaveException(ErrorId.SERIAL_CREATION_ON_STATUS);
            }
        }
        return serialOptional;
    }

    private void populateStorePartSerial(StorePartSerialInternalDto storePartSerialInternalDto, StorePartAvailability storePartAvailability,
                                         Serial serial, StorePartSerial storePartSerial) {
        storePartSerial.setSerial(serial);
        storePartSerial.setPrice(storePartSerialInternalDto.getUnitPrice());
        storePartSerial.setRackLife(storePartSerialInternalDto.getShelfLife());
        storePartSerial.setGrnNo(String.valueOf(storePartSerialInternalDto.getGrnNo()));
        storePartSerial.setPartStatus(storePartSerialInternalDto.getPartStatus());
        storePartSerial.setParentType(storePartSerialInternalDto.getParentType());
        storePartSerial.setStorePartAvailability(storePartAvailability);
        storePartSerial.setQuantity(storePartSerialInternalDto.getQuantity());
    }

    private StorePartSerial createNewStorePartSerial(Long partId, Long serialId, Long quantityReturn, Long uomId) {
        Serial serial = serialService.findByIdAndPartId(serialId, partId).orElseThrow(() ->
                EngineeringManagementServerException.badRequest(ErrorId.WRONG_SERIAL));

        StorePartAvailability availability = storePartAvailabilityService.findOrCreateAvailability(serial.getPart());
        StorePartSerialRequestDto serialRequestDto = StorePartSerialRequestDto.builder()
                .partStatus(PartStatus.NONE)
                .parentType(StorePartAvailabilityLogParentType.RETURN)
                .quantity(quantityReturn.intValue())
                .uomId(uomId)
                .build();
        return generateStoreSerial(serialRequestDto, availability, serial);
    }

    private StorePartSerial generateStoreSerial(StorePartSerialRequestDto partSerialRequestDto, StorePartAvailability availability, Serial serial) {
        StorePartSerial partSerial = populateEntity(partSerialRequestDto, new StorePartSerial(), availability, serial);
        return super.saveItem(partSerial);
    }

    private StorePartSerial populateEntity(StorePartSerialRequestDto storePartSerialRequestDto, StorePartSerial storePartSerial,
                                           StorePartAvailability storePartAvailability, Serial serial) {
        storePartSerial.setSerial(serial);
        storePartSerial.setStorePartAvailability(storePartAvailability);
        storePartSerial.setPrice(storePartSerialRequestDto.getPrice());
        if (Objects.nonNull(storePartSerialRequestDto.getCurrencyId())) {
            storePartSerial.setCurrency(currencyService.findByIdUnfiltered(storePartSerialRequestDto.getCurrencyId()));
        }
        if(Objects.nonNull(storePartSerialRequestDto.getUomId())){
            storePartSerial.setUnitMeasurement(unitMeasurementService.findByIdUnfiltered(storePartSerialRequestDto.getUomId()));
        }
        storePartSerial.setRackLife(storePartSerialRequestDto.getRackLife());
        storePartSerial.setSelfLife(storePartSerialRequestDto.getSelfLife());
        storePartSerial.setShelfLifeType(storePartSerialRequestDto.getShelfLifeType());
        storePartSerial.setGrnNo(storePartSerialRequestDto.getGrnNo());
        storePartSerial.setQuantity(storePartSerialRequestDto.getQuantity());
        storePartSerial.setPartStatus(storePartSerialRequestDto.getPartStatus());
        storePartSerial.setParentType(storePartSerialRequestDto.getParentType());

        return storePartSerial;
    }

    private StorePartSerial populateToEntity(StorePartSerialRequestDto dto, StorePartSerial entity, boolean isCreate){

        StorePartAvailability availability = Objects.nonNull(entity.getId()) ? entity.getStorePartAvailability()
                : storePartAvailabilityService.findById(dto.getAvailId());
        Part part = availability.getPart();
        PartClassification classification = part.getClassification();

        if (!Objects.equals(entity.getSerialId(), dto.getSerialId()) &&
                storePartSerialRepository.existsBySerialIdAndIsActiveTrue(dto.getSerialId())){
            throw EngineeringManagementServerException.badRequest(ErrorId.SERIAL_ALREADY_USED);
        }
        if(!Objects.equals(entity.getSerialId(), dto.getSerialId())){
            Serial serial = serialService.findById(dto.getSerialId());
            if(!Objects.equals(serial.getPartId(), part.getId())){
                throw EngineeringManagementServerException.badRequest(
                        ErrorId.PART_MUST_BE_MATCHED_FOR_SERIAL_AND_AVAILABILITY);
            }
            entity.setSerial(serial);
        }
        if(Objects.nonNull(dto.getCurrencyId()) && !Objects.equals(entity.getCurrencyId(), dto.getCurrencyId())){
            entity.setCurrency(currencyService.findById(dto.getCurrencyId()));
        }
        entity.setStorePartAvailability(availability);
        entity.setPrice(dto.getPrice());
        entity.setRackLife(dto.getRackLife());
        entity.setSelfLife(dto.getSelfLife());
        entity.setGrnNo(dto.getGrnNo());

        if (Objects.nonNull(dto.getUomId())) {
            entity.setUnitMeasurement(unitMeasurementService.findByIdUnfiltered(dto.getUomId()));
        }

        if(Objects.equals(dto.getPartStatus(), PartStatus.UNSERVICEABLE) && BooleanUtils.isTrue(dto.isIssued())){
            throw EngineeringManagementServerException.badRequest(ErrorId.UNSERVICEABLE_PART_SHOULD_NOT_BE_ISSUED);
        }
        if(Objects.equals(classification, PartClassification.CONSUMABLE) && Objects.isNull(dto.getQuantity())){
            throw EngineeringManagementServerException.badRequest(ErrorId.QUANTITY_MUST_NOT_BE_NULL_FOR_CONSUMABLE_PART);
        }
        if(!Objects.equals(classification, PartClassification.CONSUMABLE)){dto.setQuantity(INT_ONE);}
        entity.setPartStatus(dto.getPartStatus());
        entity.setParentType(BooleanUtils.isTrue(dto.isIssued()) ?
                StorePartAvailabilityLogParentType.ISSUE : StorePartAvailabilityLogParentType.DEMAND);
        updateQuantity(dto, entity, availability, isCreate);
        return entity;
    }

    private void updateQuantity(StorePartSerialRequestDto dto, StorePartSerial entity, StorePartAvailability availability, boolean isCreate) {
        if (Objects.equals(dto.getPartStatus(), PartStatus.SERVICEABLE) && BooleanUtils.isFalse(dto.isIssued())) {
            if (isCreate) {
                availability.setQuantity(availability.getQuantity() + dto.getQuantity());
            } else {
                if (!Objects.equals(dto.getQuantity(), entity.getQuantity())) {
                    availability.setQuantity((availability.getQuantity() - entity.getQuantity()) + dto.getQuantity());
                }
            }
        }
        if (Objects.equals(dto.getPartStatus(), PartStatus.SERVICEABLE) && BooleanUtils.isTrue(dto.isIssued())) {
            availability.setIssuedQuantity(availability.getIssuedQuantity() + entity.getQuantity());
        }
        entity.setQuantity(dto.getQuantity());
        storePartAvailabilityService.saveItem(availability);
    }


    public Optional<StorePartSerial> findByStorePartAvailabilityPartIdAndSerialIdAndIsActiveTrue(Long partId, Long planingSerialId) {
         return storePartSerialRepository.findByStorePartAvailabilityPartIdAndSerialIdAndIsActiveTrue(partId, planingSerialId);
    }

    public Boolean existsByCurrencyIdAndIsActiveTrue(Long currencyId) {
        return storePartSerialRepository.existsByCurrencyIdAndIsActiveTrue(currencyId);
    }

    public List<StorePartSerial> findAllByStorePartAvailabilityIdInAndIsActiveTrue(Set<Long> ids) {
        return storePartSerialRepository.findAllByStorePartAvailabilityIdInAndIsActiveTrue(ids);
    }

    public PageData getAllUnserviceableComponentList(Pageable pageable) {
        Page<UnserviceableComponentListViewModel> unserviceableComponentListViewModels = storePartSerialRepository
                .getAllUnserviceableComponentList(pageable);

        return PageData.builder()
                .model(unserviceableComponentListViewModels.getContent())
                .totalPages(unserviceableComponentListViewModels.getTotalPages())
                .totalElements(unserviceableComponentListViewModels.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public void updateExpiredStorePartSerials() {
        List<StorePartSerial> expiredStorePartSerial = getAllExpiredStorePartSerialByCurrentDate(LocalDate.now());
        if (CollectionUtils.isNotEmpty(expiredStorePartSerial)) {
            saveItemList(expiredStorePartSerial.stream().map(this::prepareExpiredStorePartSerial).collect(Collectors.toList()));
        }
    }

    public StorePartSerial prepareExpiredStorePartSerial(StorePartSerial storePartSerial) {
        storePartSerial.setPartStatus(PartStatus.UNSERVICEABLE);
        return storePartSerial;
    }

    public List<UnserviceableComponentListViewModel> getAllUnserviceableComponentListAllData() {
        return storePartSerialRepository.getAllUnserviceableComponentListAllData();
    }
    public List<StorePartSerial> getAllExpiredStorePartSerialByCurrentDate(LocalDate currentDate) {
        return storePartSerialRepository.findBySelfLifeBefore(currentDate);
    }
}
