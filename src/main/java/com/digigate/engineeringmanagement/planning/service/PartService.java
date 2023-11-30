package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.config.model.ExcelData;
import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.config.util.ExcelFileUtil;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.common.util.StringUtil;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftModelService;
import com.digigate.engineeringmanagement.planning.constant.LifeLimitUnit;
import com.digigate.engineeringmanagement.planning.constant.ModelType;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.planning.constant.PartConstant;
import com.digigate.engineeringmanagement.planning.dto.request.PartSearchDto;
import com.digigate.engineeringmanagement.planning.entity.Model;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.entity.PartWiseUom;
import com.digigate.engineeringmanagement.planning.payload.request.PartDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.repository.PartRepository;
import com.digigate.engineeringmanagement.planning.service.impl.ModelService;
import com.digigate.engineeringmanagement.planning.util.PlanningUtil;
import com.digigate.engineeringmanagement.storeinspector.service.storeinspector.StoreInspectionService;
import com.digigate.engineeringmanagement.storemanagement.constant.TransactionType;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartWiseUomProjection;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.StoreStockRoomService;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.UnitMeasurementService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ApprovalStatusService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreDemandDetailsService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartAvailabilityService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.CARD_NUMBER;
import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.TRACE_ID;
import static java.lang.Boolean.FALSE;

/**
 * Part service
 *
 * @author ashinisingha
 */
@Service
public class PartService extends AbstractService<Part, PartDto>{
    private final ModelService modelService;
    private final PartRepository repository;
    private final StoreInspectionService storeInspectionService;
    private final AircraftModelService aircraftModelService;
    private final ApprovalStatusService approvalStatusService;
    private final PartRepository partRepository;
    private final Environment environment;
    private final StoreDemandDetailsService storeDemandDetailsService;
    private final StorePartAvailabilityService  storePartAvailabilityService;
    private final UnitMeasurementService unitMeasurementService;
    private final PartWiseUomService partWiseUomService;

    /**
     * autowired constructor
     *
     * @param modelService                 {@link ModelService}
     * @param repository                   {@link PartRepository}
     * @param approvalStatusService        {@link ApprovalStatusService}
     * @param partRepository               {@link PartRepository}
     * @param storeDemandDetailsService    {@link StoreDemandDetailsService}
     * @param storePartAvailabilityService {@link StorePartAvailabilityService}
     * @param environment                  {@link Environment}
     * @param unitMeasurementService       {@link UnitMeasurementService}
     * @param partWiseUomService           {@link PartWiseUomService}
     */
    @Autowired
    public PartService(ModelService modelService,
                       StoreStockRoomService storeStockRoomService,
                       PartRepository repository,
                       @Lazy StoreInspectionService storeInspectionService,
                       AircraftModelService aircraftModelService,
                       ApprovalStatusService approvalStatusService,
                       PartRepository partRepository,
                       @Lazy StoreDemandDetailsService storeDemandDetailsService,
                       @Lazy StorePartAvailabilityService storePartAvailabilityService,
                       Environment environment,
                       UnitMeasurementService unitMeasurementService,
                       @Lazy PartWiseUomService partWiseUomService) {
        super(repository);
        this.modelService = modelService;
        this.repository = repository;
        this.storeInspectionService = storeInspectionService;
        this.aircraftModelService = aircraftModelService;
        this.approvalStatusService = approvalStatusService;
        this.partRepository = partRepository;
        this.storeDemandDetailsService = storeDemandDetailsService;
        this.storePartAvailabilityService = storePartAvailabilityService;
        this.environment = environment;
        this.unitMeasurementService = unitMeasurementService;
        this.partWiseUomService = partWiseUomService;
    }

    @Override
    public Part create(PartDto partDto) {
        partDto.setUnitOfMeasureId(partDto.getPartWiseUomIds().iterator().next());
        Part entity = convertToEntity(partDto);
        entity = saveItem(entity);
        partWiseUomService.saveAll(entity, partDto.getPartWiseUomIds());
        return entity;
    }

    @Override
    public Part update(PartDto partDto, Long id) {
        Part part = findByIdUnfiltered(id);
        partDto.setUnitOfMeasureId(findUomId(id, partDto.getPartWiseUomIds()));
        Part entity = updateEntity(partDto, part);
        entity = saveItem(entity);
        partWiseUomService.updateAll(partDto.getPartWiseUomIds(), entity, ApplicationConstant.PART);
        return entity;
    }

    private Long findUomId(Long id, List<Long> uomIds) {
        Long uomId;
        List<PartWiseUom> partWiseUom = partWiseUomService.getAllByPartId(id);
        if (uomIds.contains(partWiseUom.get(ApplicationConstant.FIRST_INDEX).getUomId())) {
            uomId = partWiseUom.get(ApplicationConstant.FIRST_INDEX).getUomId();
        } else {
            uomId = uomIds.get(ApplicationConstant.FIRST_INDEX);
        }
        return uomId;
    }

    /**
     * This method is responsible for searching part by search criteria
     *
     * @param partSearchDto {@link PartSearchDto}
     * @param pageable      {@link Pageable}
     * @return {@link Page<PartViewModel> }
     */
    public Page searchPart(PartSearchDto partSearchDto, Pageable pageable) {
        return repository.findPartBySearchCriteria(partSearchDto.getModelId(),
                PlanningUtil.setNullIfEmptyString(partSearchDto.getPartNo()), partSearchDto.getPartClassification(),
                partSearchDto.getIsActive(), pageable);
    }

    public Page<PartViewModel> searchPartWithoutAvailability(String partNo, Pageable pageable) {
        return repository.findPartBySearchCriteria(
                partNo,
                pageable);
    }

    /**
     * This method is responsible for searching part by search criteria with default page data
     *
     * @param partSearchDto {@link PartSearchDto}
     * @param pageable      {@link Pageable}
     * @return {@link Page<PartViewModel> }
     */
    public PageData search(PartSearchDto partSearchDto, Pageable pageable) {
        Page<PartViewModel> partViewModels = partSearchDto.getIsAvailPart() ?
                searchPartWithoutAvailability(partSearchDto.getPartNo(), pageable): searchPart(partSearchDto, pageable);
        return PageData.builder()
                .model(partViewModels.getContent())
                .totalPages(partViewModels.getTotalPages())
                .totalElements(partViewModels.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public PageData searchCommonPart(Pageable pageable, PartSearchDto searchDto) {
        Page<PartViewModel> partPage = repository.findPartByUniqueAircraftId(pageable, searchDto.getAircraftId(),
                searchDto.getPartNo()!=null?searchDto.getPartNo():"");
        List<PartViewModel> pageContent = partPage.getContent();
        return PageData.builder()
                .model(pageContent)
                .totalPages(partPage.getTotalPages())
                .totalElements(partPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public StockCardVM findDataForStockCard(PartSearchDto dto) {
        return convertToStockCard(repository.findDataForStockCard(dto.getPartId()), dto);
    }

    public BinCardVM findDataForBinCard(PartSearchDto dto) {
        return convertToBinCard(repository.findDataForBinCard(dto.getPartId(), dto.getPartSerialId()), dto);
    }

    public List<PartListViewModel> findAllPartByList(){
        return repository.findAllPartByList();
    }

    public PageData searchByPartAndAcType(Pageable pageable, PartSearchDto searchDto) {

        Page<PartViewModel> partPage;
        if (searchDto.getAcType() != null && searchDto.getPartClassification() != null) {
            partPage = repository.findPartByPartAndAcTypeId(searchDto.getPartClassification(),searchDto.getAcType(),
                    searchDto.getPartNo()!=null?searchDto.getPartNo():"",pageable);
        }else{
            partPage = repository.findPartByPartId(searchDto.getPartClassification(),searchDto.getPartNo()!=null?searchDto.getPartNo():"", pageable);
        }

        List<PartViewModel> pageContent = partPage.getContent();
        return PageData.builder()
                .model(pageContent)
                .totalPages(partPage.getTotalPages())
                .totalElements(partPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private void addAlternatePart(PartViewModel part) {
        if (Objects.isNull(part)) {
            return;
        }

        List<PartViewModel> partViewModelList = repository.findAlternatePartByPartId(Set.of(part.getId()));
        if (CollectionUtils.isNotEmpty(partViewModelList)) {
            partViewModelList.forEach(partViewModel -> {
                AlternatePartViewModel alternatePartViewModel = new AlternatePartViewModel();
                alternatePartViewModel.setPartNo(partViewModel.getPartNo());
                alternatePartViewModel.setId(partViewModel.getAlternatePartId());
                part.addAlternatePart(alternatePartViewModel);
            });
        }
    }

    private void addPartWiseUom(PartViewModel partViewModel) {
        if (Objects.isNull(partViewModel)) {
            return;
        }
        List<PartWiseUomProjection> partWiseUom = partWiseUomService.getAllByPartIdIn(Set.of(partViewModel.getId()));
        if (CollectionUtils.isNotEmpty(partWiseUom)) {
            List<PartWiseUomResponseDto> partWiseUomResponseDto = partWiseUom.stream().map(partWiseUomService::convertPartWiseUomResponse)
                    .collect(Collectors.toList());
            partViewModel.setPartWiseUomResponseDtoList(partWiseUomResponseDto);
        }
    }

    public Optional<PartProjection> findPartById(Long partId) {
        return repository.findPartById(partId);
    }

    public Optional<Part> findByPartId(Long partId) {
        return repository.findById(partId);
    }

    public List<PartProjection> findByIdIn(List<Long> partIds) {
        return repository.findByIdIn(partIds);
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        if (isActive == FALSE && (
                storeDemandDetailsService.existByParts(id))
                ||storePartAvailabilityService.existsByPartIdAndIsActiveTrue(id)) {
            throw new EngineeringManagementServerException(
                    ErrorId.PARENT_CAN_NOT_CHANGE_STATUS_BECAUSE_OF_CHILD_DEPENDENCY,
                    HttpStatus.PRECONDITION_FAILED,
                    MDC.get(TRACE_ID)
            );
        }
        super.updateActiveStatus(id, isActive);
    }

    /**
     * This method is responsible for getting parts by model id
     *
     * @param modelId            {@link  Long}
     * @return                   {@link List<PartViewModel> }
     */
    public List<PartViewModelLite> findAllByModelId(Long modelId){
        return repository.getAllByModelId(modelId);
    }

    public List<PartProjection> findPartByIdIn(Set<Long> partIdSet) {
        return repository.findPartByIdIn(partIdSet);
    }

    @Override
    protected PartViewModel convertToResponseDto(Part part) {
        PartViewModel partViewModel = new PartViewModel();
        partViewModel.setId(part.getId());
        partViewModel.setModelId(Objects.nonNull(part.getModel()) ? part.getModel().getId() : null);
        partViewModel.setModelName(Objects.nonNull(part.getModel()) ? part.getModel().getModelName() : null);
        partViewModel.setPartNo(part.getPartNo());
        partViewModel.setDescription(part.getDescription());
        partViewModel.setCountFactor(part.getCountFactor());
        partViewModel.setClassification(part.getClassification());
        partViewModel.setIsActive(part.getIsActive());
        partViewModel.setLifeLimit(part.getLifeLimit());
        partViewModel.setLifeLimitUnit(part.getLifeLimitUnit());
        partViewModel.setAircraftModelId(Objects.nonNull(part.getModel()) ? part.getModel().getAircraftModelId() : null);
        partViewModel.setAircraftModelName(
                Objects.nonNull(part.getModel()) && Objects.nonNull(part.getModel().getAircraftModel())
                        ? part.getModel().getAircraftModel().getAircraftModelName() : null);
        partViewModel.setModelType(Objects.nonNull(part.getModel()) ? part.getModel().getModelType() : null);
        partViewModel.setUnitOfMeasureId(part.getUnitMeasurementId());
        partViewModel.setUnitOfMeasureCode(part.getUnitMeasurement().getCode());
        addAlternatePart(partViewModel);
        addPartWiseUom(partViewModel);
        return partViewModel;
    }

    @Override
    protected Part convertToEntity(PartDto partDto) {
        return saveOrUpdateCommon(partDto, new Part());
    }

    @Override
    protected Part updateEntity(PartDto dto, Part entity) {
        return saveOrUpdateCommon(dto, entity);
    }

    @Override
    public Boolean validateClientData(PartDto partDto, Long id) {
        if(CollectionUtils.isNotEmpty(partDto.getAlternatePartIds()) && Objects.nonNull(id)) {
            if(partDto.getAlternatePartIds().contains(id)) {
                throw EngineeringManagementServerException.badRequest(ErrorId.INVALID_ALTERNATE_PARE);
            }
        }
        return Boolean.TRUE;
    }

    private Part saveOrUpdateCommon(PartDto partDto, Part part){
        if (partDto.getClassification().equals(PartClassification.ROTABLE)) {
            Model model = modelService.findById(partDto.getModelId());
            validatePartNoForRotatablePart(partDto, part, model);
            part.setModel(model);

            if(ModelType.isLLP(model.getModelType())){
                part.setLifeLimitUnit(partDto.getLifeLimitUnit());
                part.setLifeLimit(partDto.getLifeLimit());
            }
        } else if (partDto.getClassification().equals(PartClassification.CONSUMABLE)
                || partDto.getClassification().equals(PartClassification.EXPENDABLE)) {

            if(Objects.nonNull(partDto.getModelId())){
                Model model = modelService.findById(partDto.getModelId());
                part.setModel(model);
            }
            validatePartNoForConsumableAndExtendablePart(partDto.getPartNo(), part);
        }

        part.setCountFactor(partDto.getCountFactor());

        if(CollectionUtils.isNotEmpty(part.getAlternatePartSet())) {
            part.getAlternatePartSet().clear();
        }

        if(CollectionUtils.isNotEmpty(partDto.getAlternatePartIds())) {
            List<Part> alternatePartList = getAllByDomainIdIn(partDto.getAlternatePartIds(), true);
            if(partDto.getAlternatePartIds().size() != alternatePartList.size()) {
                throw EngineeringManagementServerException.notFound(ErrorId.ALTER_PART_NOT_FOUND);
            }
            part.setAlternatePartSet(new HashSet<>(alternatePartList));
        }

        part.setClassification(partDto.getClassification());
        part.setPartNo(partDto.getPartNo());
        part.setDescription(partDto.getDescription());
        part.setUnitMeasurement(unitMeasurementService.findById(partDto.getUnitOfMeasureId()));
        return part;
    }

    private void validatePartNoForRotatablePart(PartDto partDto, Part part, Model model) {
        if(Objects.isNull(part.getId())){
            if(repository.findByModelIdAndPartNo(model.getId(), partDto.getPartNo()).isPresent()){
                throw new EngineeringManagementServerException(
                        ErrorId.PART_NO_AND_MODEL_ID_ALREADY_EXISTS,
                        HttpStatus.BAD_REQUEST,
                        MDC.get(ApplicationConstant.TRACE_ID)
                );
            }
            validateAircraftModelWithPart(partDto.getPartNo(), model.getAircraftModelId());
            part.setIsActive(Boolean.TRUE);
        }else{
            if(!(part.getModel().getId().equals(partDto.getModelId()) && partDto.getPartNo().equals(part.getPartNo()))){
                if(repository.findByModelIdAndPartNo(partDto.getModelId(), partDto.getPartNo()).isPresent()){
                    throw new EngineeringManagementServerException(
                            ErrorId.PART_NO_AND_MODEL_ID_ALREADY_EXISTS,
                            HttpStatus.BAD_REQUEST,
                            MDC.get(ApplicationConstant.TRACE_ID)
                    );
                }
            }

            if (!part.getPartNo().equals(partDto.getPartNo())) {
                validateAircraftModelWithPart(partDto.getPartNo(), model.getAircraftModelId());
            }
        }
    }

    private void validateAircraftModelWithPart(String partNo, Long aircraftModelId) {
        if (partRepository.findByPartNoAndAcModelId(partNo, aircraftModelId).isPresent()) {
            throw EngineeringManagementServerException.badRequest(ErrorId.PART_IS_ALREADY_EXISTS_FOR_THIS_AC_TYPE);
        }
    }

    private void validatePartNoForConsumableAndExtendablePart(String partNo, Part part) {
        if(Objects.isNull(part.getId())){
            if(repository.findPartByPartNo(partNo).isPresent()){
                throw new EngineeringManagementServerException(
                        ErrorId.PART_IS_ALREADY_EXISTS,
                        HttpStatus.BAD_REQUEST,
                        MDC.get(ApplicationConstant.TRACE_ID)
                );
            }
            part.setIsActive(Boolean.TRUE);
        } else {
            if(!partNo.equals(part.getPartNo())){
                if(repository.findPartByPartNo(partNo).isPresent()){
                    throw new EngineeringManagementServerException(
                            ErrorId.PART_IS_ALREADY_EXISTS,
                            HttpStatus.BAD_REQUEST,
                            MDC.get(ApplicationConstant.TRACE_ID)
                    );
                }
            }
        }
    }

    public Set<Part> findAllParteByModelIdIn(Set<Long> modelIds) {
        if(CollectionUtils.isEmpty(modelIds)) {
            return Collections.emptySet();
        }
        return repository.findAllParteByModelIdIn(modelIds);
    }

    public ExcelDataResponse uploadExcel(MultipartFile file, Long aircraftModelId) {
        if(Objects.isNull(aircraftModelId)){
            throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_MODEL_ID_REQUIRED);
        }


        ExcelData excelData = ExcelFileUtil
                .getExcelDataFromSheet(file, environment.getProperty(PartConstant.ARM_EXCEL_PART), PartConstant.PART);

        if(CollectionUtils.isNotEmpty(excelData.getErrorMessages())){
            return ExcelFileUtil.prepareErrorResponse(excelData.getErrorMessages());
        }

        List<String> errorMessage = validateAndPrepareEntity(excelData, aircraftModelId);

        if(CollectionUtils.isNotEmpty(errorMessage)){
            return ExcelFileUtil.prepareErrorResponse(errorMessage);
        }

        return ExcelFileUtil.prepareSuccessResponse();
    }

    private List<String> validateAndPrepareEntity(ExcelData excelData, Long aircraftModelId) {

        List<String> errorMessages = new ArrayList<>();
        List<Part> partList = new ArrayList<>();
        if (CollectionUtils.isEmpty(excelData.getDataList())) {
            return Collections.emptyList();
        }

        Set<Model> models = modelService.findAllByAircraftModelId(aircraftModelId);
        AircraftModel aircraftModel = aircraftModelService.findById(aircraftModelId);
        List<Map<String, ?>> dataList = excelData.getDataList();

        Map<String, Model> modelMap = new HashedMap<>();
        for (Model model : models) {
            modelMap.put(model.getModelName(), model);
        }

        List<Part> partsInDb = repository.findPartByModelIdInAndIsActiveTrue(models.stream()
                .map(AbstractDomainBasedEntity::getId).collect(Collectors.toSet()));

        Set<String> partNoAndModelIdSet = new HashSet<>();
        Set<String> partNoAndAircraftModelIdSet = new HashSet<>();
        Map<String, Part> partNoToPartMap = new HashMap<>();
        Map<String, List<UnitMeasurement>> partWiseUomMap= new HashMap<>();
        for(Part part: partsInDb){
            partNoToPartMap.put(part.getPartNo(), part);
            partNoAndModelIdSet.add(part.getPartNo() + ":" + part.getModelId());
            partNoAndAircraftModelIdSet.add(part.getPartNo() + ":" + aircraftModel.getId());
        }

        Set<UnitMeasurement> unitMeasurements = unitMeasurementService.findAllUnitOfMeasures();

        Map<String, UnitMeasurement> unitMeasurementMap = unitMeasurements.stream()
                .collect(Collectors.toMap(e->e.getCode().toUpperCase(), Function.identity()));


        for (Map<String, ?> dataMap : dataList) {
            int rowNumber = Integer.parseInt(String.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER)));
            String modelName = (String) (dataMap.get(PartConstant.MODEL));
            String partNo = StringUtil.parseStringNumber(String.valueOf(dataMap.get(PartConstant.PART_NO)));
            String description = (String) (dataMap.get(PartConstant.DESCRIPTION));
            Double countFactor = (Double) dataMap.get(PartConstant.COUNT_FACTOR);
            PartClassification classification = (PartClassification) (dataMap.get(PartConstant.CLASSIFICATION));

            if (classification.equals(PartClassification.ROTABLE) && StringUtils.isBlank(modelName)) {
                errorMessages.add(String.format("ROTABLE parts must have model. Part no: {%s}, at row {%s}",
                        partNo, rowNumber));
            }

            if (classification.equals(PartClassification.ROTABLE) && StringUtils.isNotBlank(modelName)
                    && !modelMap.containsKey(modelName)) {
                errorMessages.add(String.format("Model not found for rotable part. Part no: {%s}, Model name: " +
                        "{%s}, at row {%s}", partNo, modelName, rowNumber));
            }

            if (classification.equals(PartClassification.CONSUMABLE) && StringUtils.isNotBlank(modelName)) {
                errorMessages.add(String.format("CONSUMABLE parts cant have model. Part no: {%s}, at row {%s}",
                        partNo, rowNumber));
            }

            if (classification.equals(PartClassification.EXPENDABLE) && StringUtils.isNotBlank(modelName)) {
                errorMessages.add(String.format("EXPENDABLE parts cant have model. Part no: {%s}, at row {%s}",
                        partNo, rowNumber));
            }
            UnitMeasurement unitMeasurement = new UnitMeasurement();
            String partWiseUomCodes = (String) dataMap.getOrDefault(PartConstant.UNIT_OF_MEASURE, null);
            if (Objects.nonNull(partWiseUomCodes)) {
                List<String> uomCodeList = Arrays.stream(partWiseUomCodes.split(",")).map(String::toUpperCase).collect(Collectors.toList());
                unitMeasurement = unitMeasurementMap.get(uomCodeList.get(ApplicationConstant.FIRST_INDEX));
                setExcelPartWiseUom(uomCodeList, unitMeasurementMap, errorMessages, partWiseUomMap, modelName, partNo);
            }


            String alternatePartsList = (String) (dataMap.get(PartConstant.ALTERNATE_PARTS));
            LifeLimitUnit lifeLimitUnit = (LifeLimitUnit) (dataMap.get(PartConstant.LIFE_LIMIT_UNIT));
            Long lifeLimit = (Long) (dataMap.get(PartConstant.LIFE_LIMIT));

            boolean isValid = true;

            if (modelMap.containsKey(modelName)) {
                String partNameAndModelId = partNo + ":" + modelMap.get(modelName).getId();
                String partNameAndAircraftModelId = partNo + ":" + aircraftModel.getId();
                if (partNoAndModelIdSet.contains(partNameAndModelId)) {
                    errorMessages.add(String.format("Part No{%s} & model{%s} already exists in database at row : {%s}",
                            partNo, modelName, rowNumber));
                    isValid = false;
                }

                if (partNoAndAircraftModelIdSet.contains(partNameAndAircraftModelId)) {
                    errorMessages.add(String.format("Part No{%s} & Aircraft-Model{%s} already exists in database at row : {%s}",
                            partNo, aircraftModel.getAircraftModelName(), rowNumber));
                    isValid = false;
                }
            } else if (StringUtils.isBlank(modelName) && StringUtils.isBlank(partNo)){
                errorMessages.add(String.format("Model{%s} is not valid at row : {%s}",modelName, rowNumber));
                isValid = false;
            }

            Set<Part> alternateParts = new HashSet<>();
            if(StringUtils.isNotBlank(alternatePartsList)){
                for(String alternatePartNo : Arrays.stream((alternatePartsList.trim().split("\\s*,\\s*")))
                        .collect(Collectors.toSet())){
                    if( !partNoToPartMap.containsKey(alternatePartNo) ){
                        errorMessages.add(String.format("Alternate part{%s} it not valid at row : {%s}"
                                ,alternatePartNo, rowNumber) );
                        isValid = false;
                        break;
                    }
                    alternateParts.add(partNoToPartMap.get(alternatePartNo));
                }
            }

            if (isValid) {
                partList.add(preparePart(
                        modelMap.get(modelName), partNo, description,
                        countFactor,
                        classification,
                        alternateParts,
                        lifeLimitUnit,
                        lifeLimit,
                        unitMeasurement
                ));
            }
        }

        if(CollectionUtils.isEmpty(errorMessages) && CollectionUtils.isNotEmpty(partList)){
            List<Part> parts = saveItemList(partList);
            partWiseUomService.saveExcelPartWiseUom(parts, partWiseUomMap);
        }
        return errorMessages;
    }

    private void setExcelPartWiseUom(List<String> uomCodeList, Map<String, UnitMeasurement> unitMeasurementMap,
                                     List<String> errorMessages, Map<String, List<UnitMeasurement>> partWiseUomMap,
                                     String modelName, String partNo) {

        List<UnitMeasurement> unitMeasurementList = new ArrayList<>();
        uomCodeList.forEach(code -> {
            UnitMeasurement unitMeasurement = unitMeasurementMap.getOrDefault(code, null);
            if (Objects.isNull(unitMeasurement)) {
                errorMessages.add(String.format("Invalid unit of measure {%s}", code));
            } else {
                unitMeasurementList.add(unitMeasurement);
            }
        });
        partWiseUomMap.put(partNo + modelName, unitMeasurementList);
    }

    private Part preparePart(Model model, String partNo, String description, Double countFactor,
                             PartClassification classification, Set<Part> alternateParts, LifeLimitUnit lifeLimitUnit,
                             Long lifeLimit , UnitMeasurement unitMeasurement){
        Part part = new Part();
        part.setModel(model);
        part.setPartNo(partNo);
        part.setDescription(description);
        part.setCountFactor(countFactor);
        part.setUnitMeasurement(unitMeasurement);
        part.setClassification(classification);
        part.setAlternatePartSet(alternateParts);

        if(Objects.nonNull(model) && ModelType.isLLP(model.getModelType())){
            part.setLifeLimitUnit(lifeLimitUnit);
            part.setLifeLimit(lifeLimit);
        }

        part.setIsActive(true);
        return part;
    }

    /**
     * responsible for finding all consumable pars
     *
     * @return Part View Model
     */
    public List<PartViewModel> findAllConsumablePart() {
        return repository.findAllPartByPartType(PartClassification.CONSUMABLE);
    }

    public List<Part> findConsumableParts() {
        return repository.findAllPartsByPartType(PartClassification.CONSUMABLE);
    }

    public Part findByPartNo(String partNo) {
        return repository.findByPartNoAndIsActiveTrue(partNo);
    }

    public PartViewModelLite findByPartNoAndAircraftModelId(String partNumber, Long aircraftModelId) {
        return repository.findByPartNoAndAircraftModelId(partNumber,aircraftModelId);
    }

    public List<PartViewModelLite> searchByPartNo(String partNo) {
        return repository.searchByPartNo(partNo);
    }

    public List<Part> findAllByAircraftModelId(Long aircraftModelId){
        return repository.findAllByAircraftModelId(aircraftModelId);
    }

    public List<PartViewModelLite> getPartListByAcTypeOfAircraftId(Long aircraftId){
        return repository.getPartListByAcTypeOfAircraftId(aircraftId);
    }

    public PartViewModelLite getByPartNo(String partNumber) {
        return repository.getByPartNo(partNumber);
    }

    public PageData searchPartDetailsWithDemand(PartSearchDto dto, Pageable pageable) {
        Page<DashboardPartDemandViewModel> partPage = repository.findPartDetailsFromDemand(dto.getPartId(), dto.getVoucherNo(), pageable);
        List<DashboardPartDemandViewModel> pageContent = partPage.getContent();
        return PageData.builder()
                .model(pageContent)
                .totalPages(partPage.getTotalPages())
                .totalElements(partPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public PageData searchPartDetailsWithIssue(PartSearchDto dto, Pageable pageable) {
        Page<DashboardPartIssueViewModel> partPage = repository.findPartDetailsFromIssue(dto.getPartId(), dto.getVoucherNo(), pageable);
        List<DashboardPartIssueViewModel> pageContent = partPage.getContent();
        return PageData.builder()
                .model(pageContent)
                .totalPages(partPage.getTotalPages())
                .totalElements(partPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public PageData searchPartDetailsWithRequisition(PartSearchDto dto, Pageable pageable) {
        Page<DashboardPartRequisitionViewModel> partPage = repository.findPartDetailsFromRequisition(dto.getPartId(), dto.getVoucherNo(), pageable);
        List<DashboardPartRequisitionViewModel> pageContent = partPage.getContent();
        return PageData.builder()
                .model(pageContent)
                .totalPages(partPage.getTotalPages())
                .totalElements(partPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public PageData searchPartDetailsWithScrap(PartSearchDto dto, Pageable pageable) {
        Page<DashboardPartScrapViewModel> partPage = repository.findPartDetailsFromScrap(dto.getPartId(), dto.getVoucherNo(), pageable);
        List<DashboardPartScrapViewModel> pageContent = partPage.getContent();
        return PageData.builder()
                .model(pageContent)
                .totalPages(partPage.getTotalPages())
                .totalElements(partPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public PageData searchPartDetailsWithAvailability(PartSearchDto dto, Pageable pageable) {
        Page<DashboardPartAvailabilityViewModel> partPage = repository.findPartDetailsFromAvailability(dto.getPartId(), pageable);
        List<DashboardPartAvailabilityViewModel> pageContent = partPage.getContent();
        return PageData.builder()
                .model(pageContent)
                .totalPages(partPage.getTotalPages())
                .totalElements(partPage.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    private StockCardVM convertToStockCard(List<StockCardViewModel> stockCardViewModels, PartSearchDto dto) {
        StockCardVM stockCardVM = new StockCardVM();

        stockCardViewModels.forEach(stockCardViewModel -> {
            stockCardVM.setId(stockCardViewModel.getId());
            stockCardVM.setPartNo(stockCardViewModel.getPartNo());
            stockCardVM.setAlternatePart(repository.findAlternatePartById(dto.getPartId()));
            stockCardVM.setDescription(stockCardViewModel.getDescription());
            stockCardVM.setUomCode(stockCardViewModel.getUomCode());
            stockCardVM.setRackCode(stockCardViewModel.getRackCode());
            stockCardVM.setRackRowCode(stockCardViewModel.getRackRowCode());
            stockCardVM.setRackRowBinCode(stockCardViewModel.getRackRowBinCode());
            stockCardVM.setOfficeCode(stockCardViewModel.getOfficeCode());
            stockCardVM.setOtherLocation(stockCardViewModel.getOtherLocation());
            stockCardVM.setMinStock(stockCardViewModel.getMinStock());
            stockCardVM.setMaxStock(stockCardViewModel.getMaxStock());
            stockCardVM.setAircraftModelName(stockCardViewModel.getAircraftModelName());
            stockCardVM.setStockRoomId(stockCardViewModel.getStockRoomId());
            stockCardVM.setStockRoomCode(stockCardViewModel.getStockRoomCode());
            stockCardVM.setNumber(CARD_NUMBER + stockCardViewModel.getId());
            stockCardVM.setIcName(approvalStatusService.findUserNameByWorkflowIdAndStatusType(stockCardViewModel.getWfaId(),
                    stockCardViewModel.getParentId(), getApprovalStatusType(stockCardViewModel.getTransactionType())));
            stockCardVM.getStockDataList().add(
                    StockData.builder()
                            .storePartSerialId(stockCardViewModel.getStorePartSerialId())
                            .partStatus(stockCardViewModel.getPartStatus())
                            .issuedQty(stockCardViewModel.getIssuedQty())
                            .parentType(stockCardViewModel.getParentType())
                            .serialId(stockCardViewModel.getSerialId())
                            .serialNumber(stockCardViewModel.getSerialNumber())
                            .logId(stockCardViewModel.getLogId())
                            .unitPrice(stockCardViewModel.getUnitPrice())
                            .issuedQty(stockCardViewModel.getIssuedQty())
                            .inStock(stockCardViewModel.getInStock())
                            .voucherNo(stockCardViewModel.getVoucherNo())
                            .createdAt(stockCardViewModel.getCreatedAt())
                            .receivedQty(stockCardViewModel.getReceivedQty())
                            .submittedUser(stockCardViewModel.getSubmittedByUserLogin())
                            .vendorName(storeInspectionService.findVendorByPartIdAndSerialId(dto.getPartId(),
                                    stockCardViewModel.getStorePartSerialId()))
                            .build());
        });
        return stockCardVM;
    }

    private BinCardVM convertToBinCard(List<BinCardViewModel> viewModelList, PartSearchDto dto) {
        BinCardVM binCardVM = new BinCardVM();

        viewModelList.forEach(binCardViewModel -> {
            binCardVM.setId(binCardViewModel.getId());
            binCardVM.setAlternatePart(repository.findAlternatePartById(dto.getPartId()));
            binCardVM.setPartNo(binCardViewModel.getPartNo());
            binCardVM.setPartClassification(binCardViewModel.getClassification());
            binCardVM.setDescription(binCardViewModel.getDescription());
            binCardVM.setUomCode(binCardViewModel.getUomCode());
            binCardVM.setRackCode(binCardViewModel.getRackCode());
            binCardVM.setRackRowCode(binCardViewModel.getRackRowCode());
            binCardVM.setRackRowBinCode(binCardViewModel.getRackRowBinCode());
            binCardVM.setOtherLocation(binCardViewModel.getOtherLocation());
            binCardVM.setOfficeCode(binCardViewModel.getOfficeCode());
            binCardVM.setAircraftModelName(binCardViewModel.getAircraftModelName());
            binCardVM.setNumber(CARD_NUMBER + binCardViewModel.getId());
            binCardVM.setMinStock(binCardViewModel.getMinStock());
            binCardVM.getBinDataList().add(
                    BinData.builder()
                            .storePartSerialId(binCardViewModel.getStorePartSerialId())
                            .tso(binCardViewModel.getTso())
                            .issuedQty(binCardViewModel.getIssuedQty())
                            .grn(binCardViewModel.getGrn())
                            .serialId(binCardViewModel.getSerialId())
                            .serialNumber(binCardViewModel.getSerialNumber())
                            .logId(binCardViewModel.getLogId())
                            .inStock(binCardViewModel.getInStock())
                            .voucherNo(binCardViewModel.getVoucherNo())
                            .createdAt(binCardViewModel.getCreatedAt())
                            .receivedQty(binCardViewModel.getReceivedQty())
                            .selfLife(binCardViewModel.getSelfLife())
                            .submittedUser(binCardViewModel.getSubmittedByUserLogin())
                            .vendorName(storeInspectionService.findVendorByPartIdAndSerialId(dto.getPartId(), dto.getPartSerialId()))
                            .build());
        });
        return binCardVM;
    }

    private ApprovalStatusType getApprovalStatusType(TransactionType transactionType) {
        ApprovalStatusType approvalStatusType = null;
        switch (transactionType) {
            case ISSUE:
                approvalStatusType = ApprovalStatusType.STORE_ISSUE;
                break;
            case RECEIVE:
                approvalStatusType = ApprovalStatusType.STORE_RETURN;
                break;
            case SCRAP:
                approvalStatusType = ApprovalStatusType.STORE_SCRAP;
                break;
        }
        return approvalStatusType;
    }
}
