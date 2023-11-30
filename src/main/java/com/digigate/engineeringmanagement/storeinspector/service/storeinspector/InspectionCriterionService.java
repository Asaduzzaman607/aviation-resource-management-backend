package com.digigate.engineeringmanagement.storeinspector.service.storeinspector;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.storeinspector.payload.projection.InspectionChecklistProjection;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.InspectionChecklist;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.InspectionCriterion;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.StoreInspection;
import com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector.InspectionCriterionRequestDto;
import com.digigate.engineeringmanagement.storeinspector.payload.response.storeinspector.InspectionCriterionResponseDto;
import com.digigate.engineeringmanagement.storeinspector.repository.storeinspector.InspectionCriterionRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InspectionCriterionService extends AbstractSearchService<InspectionCriterion, InspectionCriterionRequestDto, IdQuerySearchDto> {

    private final StoreInspectionService storeInspectionService;
    private final InspectionChecklistService inspectionChecklistService;
    private final InspectionCriterionRepository inspectionCriterionRepository;


    public InspectionCriterionService(AbstractRepository<InspectionCriterion> repository,
                                      InspectionCriterionRepository inspectionCriterionRepository,
                                      @Lazy StoreInspectionService storeInspectionService,
                                      InspectionChecklistService inspectionChecklistService) {
        super(repository);
        this.inspectionCriterionRepository = inspectionCriterionRepository;
        this.storeInspectionService = storeInspectionService;
        this.inspectionChecklistService = inspectionChecklistService;
    }

    public boolean existsByInspectionIdAndIsActiveTrue(Long id) {
        return inspectionCriterionRepository.existsByInspectionIdAndIsActiveTrue(id);
    }

    public List<InspectionCriterion> findByInspectionIdIn(Set<Long> id) {
        return inspectionCriterionRepository.findByInspectionIdInAndIsActiveTrue(id);
    }

    public boolean existsByDescriptionIdAndIsActiveTrue(Long id) {
        return inspectionCriterionRepository.existsByDescriptionIdAndIsActiveTrue(id);
    }

    /**
     * Custom response data method
     *
     * @param inspectionCriterion
     * @return response data
     */
    public List<InspectionCriterionResponseDto> getResponse(List<InspectionCriterion> inspectionCriterion) {

        Set<Long> collectionOfInspectionCheckListIds = inspectionCriterion.stream().map(InspectionCriterion::getDescriptionId)
                .collect(Collectors.toSet());

        Map<Long, InspectionChecklistProjection> inspectionChecklistProjectionMap = inspectionChecklistService
                .findDescriptionByIdIn(collectionOfInspectionCheckListIds).stream().collect(Collectors
                        .toMap(InspectionChecklistProjection::getId, Function.identity()));

        return inspectionCriterion.stream().map(criterion ->
                        convertToResponseDto(criterion, inspectionChecklistProjectionMap.get(criterion.getDescriptionId())))
                .collect(Collectors.toList());
    }

    /**
     * Custom save data method
     *
     * @param storeInspection
     * @param inspectionCriterionRequestDto
     * @return successfully save data
     */
    public void saveAll(List<InspectionCriterionRequestDto> inspectionCriterionRequestDto, StoreInspection storeInspection) {
        List<InspectionCriterion> inspectionCriterion = populateDtoListToEntityList(inspectionCriterionRequestDto, storeInspection);
        super.saveItemList(inspectionCriterion);
    }

    @Override
    protected InspectionCriterionResponseDto convertToResponseDto(InspectionCriterion inspectionCriterion) {
        return null;
    }

    @Override
    protected InspectionCriterion convertToEntity(InspectionCriterionRequestDto inspectionCriterionRequestDto) {
        return null;
    }

    @Override
    protected InspectionCriterion updateEntity(InspectionCriterionRequestDto dto, InspectionCriterion entity) {
        return null;
    }

    @Override
    protected Specification<InspectionCriterion> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<InspectionCriterion> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.ENTITY_CODE));
    }

    private InspectionCriterionResponseDto convertToResponseDto(InspectionCriterion inspectionCriterion,
                                                                InspectionChecklistProjection inspectionChecklistProjection) {

        InspectionCriterionResponseDto dto = InspectionCriterionResponseDto.builder()
                .id(inspectionCriterion.getId())
                .inspectionId(inspectionCriterion.getInspectionId())
                .inspectionStatus(inspectionCriterion.getInspectionStatus())
                .isActive(inspectionCriterion.getIsActive())
                .build();

        if (Objects.nonNull(inspectionChecklistProjection)) {
            dto.setDescriptionId(inspectionChecklistProjection.getId());
            dto.setDescription(inspectionChecklistProjection.getDescription());
        }
        return dto;

    }

    private List<InspectionCriterion> populateDtoListToEntityList(List<InspectionCriterionRequestDto> inspectionCriterionRequestDto,
                                                                  StoreInspection storeInspection) {
        Set<Long> inspectionChecklist = inspectionCriterionRequestDto.stream()
                .map(InspectionCriterionRequestDto::getDescriptionId).collect(Collectors.toSet());

        Map<Long, InspectionChecklist> inspectionChecklistsMap = inspectionChecklistService
                .getAllByDomainIdIn(inspectionChecklist, true).stream()
                .collect(Collectors.toMap(InspectionChecklist::getId, Function.identity()));


        Set<Long> updateIdList = inspectionCriterionRequestDto.stream().map(InspectionCriterionRequestDto::getId)
                .filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, InspectionCriterion> inspectionCriterionsMap = inspectionCriterionRepository.findAllByIdIn(updateIdList)
                .stream().collect(Collectors.toMap(InspectionCriterion::getId, Function.identity()));

        return inspectionCriterionRequestDto.stream().map(inchecklist -> {
                    InspectionChecklist inspectionChecklists = inspectionChecklistsMap.get(inchecklist.getDescriptionId());

                    if (Objects.isNull(inspectionChecklists)) {
                        throw EngineeringManagementServerException.notFound(ErrorId.DESCRIPTION_NOT_FOUND);
                    }

                    return populateToEntity(inchecklist, inspectionCriterionsMap.getOrDefault(inchecklist.getId(),
                            new InspectionCriterion()), storeInspection, inspectionChecklists);
                })
                .collect(Collectors.toList());
    }


    private InspectionCriterion populateToEntity(InspectionCriterionRequestDto inspectionCriterionRequestDto,
                                                 InspectionCriterion inspectionCriterion, StoreInspection storeInspection,
                                                 InspectionChecklist inspectionChecklist) {

        inspectionCriterion.setInspectionChecklist(inspectionChecklist);
        inspectionCriterion.setInspectionStatus(inspectionCriterionRequestDto.getInspectionStatus());
        inspectionCriterion.setStoreInspection(storeInspection);
        inspectionCriterion.setIsActive(inspectionCriterionRequestDto.getIsActive());
        return inspectionCriterion;
    }

}
