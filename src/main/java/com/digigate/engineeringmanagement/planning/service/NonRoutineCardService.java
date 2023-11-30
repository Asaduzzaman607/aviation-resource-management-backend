package com.digigate.engineeringmanagement.planning.service;


import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.entity.AircraftCheck;
import com.digigate.engineeringmanagement.planning.entity.AircraftCheckIndex;
import com.digigate.engineeringmanagement.planning.entity.NonRoutineCard;
import com.digigate.engineeringmanagement.planning.payload.request.AMLDefectRectificationDto;
import com.digigate.engineeringmanagement.planning.payload.request.NonRoutineCardDto;
import com.digigate.engineeringmanagement.planning.payload.request.NonRoutineCardSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.NonRoutineCardViewModel;
import com.digigate.engineeringmanagement.planning.repository.NonRoutineCardRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Non Routine Card Service
 *
 * @author ashinisingha
 */
@Service
public class NonRoutineCardService extends AbstractSearchService<NonRoutineCard, NonRoutineCardDto,
        NonRoutineCardSearchDto> implements NonRoutineCardIService {

    private static final String PLUS_DELIMITER = "+";
    private static final String AIRCRAFT_ID = "aircraftId";
    private static final String IS_ACTIVE = "isActive";
    private final AmlDefectRectificationService amlDefectRectificationService;
    private final AircraftService aircraftService;
    private final AircraftCheckIndexService aircraftCheckIndexService;
    private final NonRoutineCardRepository nonRoutineCardRepository;

    /**
     * Autowired constructor
     * @param repository                     {@link AbstractRepository<NonRoutineCard>}
     * @param amlDefectRectificationService  {@link AmlDefectRectificationService}
     * @param aircraftService                {@link AircraftService}
     * @param aircraftCheckIndexService      {@link AircraftCheckIndexService}
     * @param nonRoutineCardRepository       {@link NonRoutineCardRepository}
     */
    @Autowired
    public NonRoutineCardService(AbstractRepository<NonRoutineCard> repository,
                                 AmlDefectRectificationService amlDefectRectificationService,
                                 AircraftService aircraftService, AircraftCheckIndexService aircraftCheckIndexService,
                                 NonRoutineCardRepository nonRoutineCardRepository) {
        super(repository);
        this.amlDefectRectificationService = amlDefectRectificationService;
        this.aircraftService = aircraftService;
        this.aircraftCheckIndexService = aircraftCheckIndexService;
        this.nonRoutineCardRepository = nonRoutineCardRepository;
    }

    @Override
    protected Specification<NonRoutineCard> buildSpecification(NonRoutineCardSearchDto searchDto) {
        CustomSpecification<NonRoutineCard> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.equalSpecificationAtRoot(searchDto.getAircraftId(), AIRCRAFT_ID)
                        .and(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE))
        );
    }

    @Transactional
    @Override
    public NonRoutineCard create(NonRoutineCardDto nonRoutineCardDto) {
        NonRoutineCard nonRoutineCard = super.saveItem(convertToEntity(nonRoutineCardDto));
        saveOrUpdateAmlDefectRectification(nonRoutineCard.getId(), nonRoutineCardDto.getAmlDefectRectificationDto(),
                Boolean.FALSE);
        return  nonRoutineCard;
    }

    @Transactional
    @Override
    public NonRoutineCard update(NonRoutineCardDto nonRoutineCardDto, Long id) {
        NonRoutineCard nonRoutineCard = super.findById(id);
        NonRoutineCard updatedNonRoutineCard = super.saveItem(
                updateEntity(nonRoutineCardDto, nonRoutineCard));
        saveOrUpdateAmlDefectRectification(updatedNonRoutineCard.getId(),
                nonRoutineCardDto.getAmlDefectRectificationDto(), Boolean.TRUE);

        return updatedNonRoutineCard;
    }

    @Override
    protected NonRoutineCardViewModel  convertToResponseDto(NonRoutineCard nonRoutineCard) {
        NonRoutineCardViewModel nonRoutineCardViewModel = new NonRoutineCardViewModel();
        nonRoutineCardViewModel.setNonRoutineCardId(nonRoutineCard.getId());
        nonRoutineCardViewModel.setAcCheckIndexId(nonRoutineCard.getAcCheckIndexId());

        if(Objects.nonNull(nonRoutineCard.getAcCheckIndexId())){
            nonRoutineCardViewModel.setAircraftChecksName(prepareAircraftChecks(aircraftCheckIndexService
                    .findById(nonRoutineCard.getAcCheckIndexId())));
        }

        if(Objects.nonNull(nonRoutineCard.getNrcNo())){
           nonRoutineCardViewModel.setAmlDefectRectificationModelView(
                   amlDefectRectificationService.findDefectRectificationByNrcId(nonRoutineCard.getId()));
        }

        nonRoutineCardViewModel.setNonRoutineCardId(nonRoutineCard.getId());
        nonRoutineCardViewModel.setAircraftId(nonRoutineCard.getAircraftId());
        nonRoutineCardViewModel.setAircraftName(nonRoutineCard.getAircraft().getAircraftName());
        nonRoutineCardViewModel.setNrcNo(nonRoutineCard.getNrcNo());
        nonRoutineCardViewModel.setReference(nonRoutineCard.getReference());
        nonRoutineCardViewModel.setIssueDate(nonRoutineCard.getIssueDate());
        nonRoutineCardViewModel.setIsActive(nonRoutineCard.getIsActive());

        return nonRoutineCardViewModel;
    }

    @Override
    protected NonRoutineCard convertToEntity(NonRoutineCardDto nonRoutineCardDto) {

        return saveUpdateCommon(new NonRoutineCard(), nonRoutineCardDto);
    }

    @Override
    protected NonRoutineCard updateEntity(NonRoutineCardDto dto, NonRoutineCard entity) {
        return saveUpdateCommon(entity, dto);
    }

    private NonRoutineCard saveUpdateCommon(NonRoutineCard nonRoutineCard, NonRoutineCardDto nonRoutineCardDto){
        Aircraft aircraft = aircraftService.findById(nonRoutineCardDto.getAircraftId());

        if(Objects.nonNull(nonRoutineCardDto.getAcCheckIndexId())){
            AircraftCheckIndex aircraftCheckIndex =
                    aircraftCheckIndexService.findById(nonRoutineCardDto.getAcCheckIndexId());
            nonRoutineCard.setAircraftCheckIndex(aircraftCheckIndex);
        }

        nonRoutineCard.setAircraft(aircraft);
        validateAndSetNrcNo(nonRoutineCardDto.getNrcNo(), nonRoutineCard);
        nonRoutineCard.setReference(nonRoutineCardDto.getReference());
        nonRoutineCard.setIssueDate(nonRoutineCardDto.getIssueDate());
        return nonRoutineCard;
    }

    private void validateAndSetNrcNo(String nrcNo, NonRoutineCard nonRoutineCard) {
        if (StringUtils.isNotBlank(nonRoutineCard.getNrcNo()) && nrcNo.equals(nonRoutineCard.getNrcNo())) {
            return;
        }

        Optional<Long> nrcIdOptional = nonRoutineCardRepository.findNrcIdByNrcNo(nrcNo);

        if (nrcIdOptional.isPresent()) {
            throw new EngineeringManagementServerException(ErrorId.DUPLICATE_NRC_NO, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }

        nonRoutineCard.setNrcNo(nrcNo);
    }

    private void saveOrUpdateAmlDefectRectification(Long nrcId, AMLDefectRectificationDto amlDefectRectificationDto,
                                                    Boolean isUpdate){
        amlDefectRectificationDto.setNrcId(nrcId);
        List<AMLDefectRectificationDto> amlDefectRectificationDtos = new ArrayList<>();
        amlDefectRectificationDtos.add(amlDefectRectificationDto);
        if (isUpdate == Boolean.TRUE) {
            amlDefectRectificationService.update(amlDefectRectificationDtos);
        } else {
            amlDefectRectificationService.create(amlDefectRectificationDtos);
        }
    }

    private String prepareAircraftChecks(AircraftCheckIndex aircraftCheckIndex) {
        List<String> acCheckIndexNameList = new ArrayList<>();
        Set<AircraftCheck> aircraftCheckSet = aircraftCheckIndex.getAircraftTypeCheckSet();
        aircraftCheckSet.forEach(
                aircraftCheck -> {
                    String acCheckIndexName = Objects.nonNull(aircraftCheck.getCheck()) ? aircraftCheck.getCheck()
                            .getTitle() : null;
                    if(Objects.nonNull(acCheckIndexName)){
                        acCheckIndexNameList.add(acCheckIndexName);
                    }
                }
        );
        List<String> sortedAcCheckIndexNameList = acCheckIndexNameList.stream().sorted().collect(Collectors.toList());
        return String.join(PLUS_DELIMITER, sortedAcCheckIndexNameList);
    }
}
