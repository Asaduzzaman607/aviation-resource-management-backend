package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.config.model.ExcelData;
import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.config.util.ExcelFileUtil;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.StringUtil;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftModelService;
import com.digigate.engineeringmanagement.planning.entity.AircraftLocation;
import com.digigate.engineeringmanagement.planning.entity.Model;
import com.digigate.engineeringmanagement.planning.entity.ModelTree;
import com.digigate.engineeringmanagement.planning.entity.Position;
import com.digigate.engineeringmanagement.planning.payload.request.ModelTreePayload;
import com.digigate.engineeringmanagement.planning.payload.request.ModelTreeSearchPayload;
import com.digigate.engineeringmanagement.planning.payload.request.PositionDto;
import com.digigate.engineeringmanagement.planning.payload.response.ModelTreeExcelViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.ModelTreeViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.PositionModelView;
import com.digigate.engineeringmanagement.planning.repository.ModelTreeRepository;
import com.digigate.engineeringmanagement.planning.service.AircraftLocationService;
import com.digigate.engineeringmanagement.planning.service.ModelTreeIService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Model Tree Service
 *
 * @author Masud Rana
 */
@Service
public class ModelTreeService extends AbstractSearchService<ModelTree, ModelTreePayload, ModelTreeSearchPayload>
        implements ModelTreeIService {
    private final ModelService modelService;
    private final AircraftLocationService aircraftLocationService;
    private final ModelTreeRepository modelTreeRepository;
    private final PositionServiceImpl positionService;
    private final Environment environment;
    private final AircraftModelService aircraftModelService;

    private final String MODEL_ID = "modelId";
    private final String LOCATION_ID = "locationId";
    private final String HIGHER_MODEL_ID = "higherModelId";
    private final String POSITION_ID = "positionId";
    private final String IS_ACTIVE = "isActive";
    public static final String MODEL_TREE = "Model Tree";
    private static final String MODEL = "Model";
    private static final String HIGHER_MODEL = "Higher Model";
    private static final String LOCATION = "Location";
    private static final String POSITION = "Position";
    private static final String ARM_EXCEL_MODEL_TREE = "arm.excel.upload.file.name.modelTree";

    /**
     * Autowired constructor
     *
     * @param repository              {@link AbstractRepository}
     * @param modelService            {@link ModelService}
     * @param aircraftLocationService {@link AircraftLocationService}
     * @param modelTreeRepository     {@link ModelTreeRepository}
     * @param positionService         {@link PositionServiceImpl}
     * @param environment             {@link Environment}
     * @param aircraftModelService
     */
    public ModelTreeService(AbstractRepository<ModelTree> repository,
                            ModelService modelService,
                            AircraftLocationService aircraftLocationService,
                            ModelTreeRepository modelTreeRepository,
                            PositionServiceImpl positionService, Environment environment, AircraftModelService aircraftModelService) {
        super(repository);
        this.modelService = modelService;
        this.aircraftLocationService = aircraftLocationService;
        this.modelTreeRepository = modelTreeRepository;
        this.positionService = positionService;
        this.environment = environment;
        this.aircraftModelService = aircraftModelService;
    }

    /**
     * convert response  from entity
     *
     * @param modelTree {@link ModelTree}
     * @return {@link ModelTreeViewModel}
     */
    @Override
    protected ModelTreeViewModel convertToResponseDto(ModelTree modelTree) {

        ModelTreeViewModel modelTreeViewModel = ModelTreeViewModel.builder()
                .id(modelTree.getId())
                .modelId(modelTree.getModelId())
                .aircraftModelId(modelTree.getModel().getAircraftModelId())
                .higherModelId(modelTree.getHigherModelId())
                .positionId(modelTree.getPositionId())
                .locationId(modelTree.getLocationId())
                .createdAt(modelTree.getCreatedAt())
                .isActive(modelTree.getIsActive())
                .build();
        addModel(modelTree, modelTreeViewModel);
        addHigherModel(modelTree, modelTreeViewModel);
        addLocation(modelTree, modelTreeViewModel);
        addPosition(modelTree, modelTreeViewModel);
        return modelTreeViewModel;
    }

    /**
     * convert entity  from dto
     *
     * @param modelTreePayload {@link ModelTreePayload}
     * @return {@link ModelTree}
     */
    @Override
    protected ModelTree convertToEntity(ModelTreePayload modelTreePayload) {
        return prepareModelTree(modelTreePayload, new ModelTree());
    }

    /**
     * validate client data
     *
     * @param modelTreePayload {@link ModelTreePayload}
     * @param id               {@link Long}
     */
    @Override
    public Boolean validateClientData(ModelTreePayload modelTreePayload, Long id) {
        if (modelTreePayload.getHigherModelId().equals(modelTreePayload.getModelId())) {
            throw new EngineeringManagementServerException(
                    ErrorId.MODEL_CAN_NOT_BE_HIGHER_MODEL_ITSELF,
                    HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        hasDuplicateModelTree(modelTreePayload, id);
        hasReverseModelTree(modelTreePayload, id);
        return Boolean.TRUE;
    }

    /**
     * convert entity  from dto
     *
     * @param modelTreePayload {@link ModelTreePayload}
     * @param modelTree        {@link ModelTree}
     * @return {@link ModelTree}
     */
    @Override
    protected ModelTree updateEntity(ModelTreePayload modelTreePayload, ModelTree modelTree) {
        return prepareModelTree(modelTreePayload, modelTree);
    }

    private ModelTree prepareModelTree(ModelTreePayload modelTreePayload, ModelTree modelTree) {
        modelTree.setModel(findModelById(modelTreePayload.getModelId()));
        if (Objects.nonNull(modelTreePayload.getPositionId())) {
            modelTree.setPosition(positionService.findById(modelTreePayload.getPositionId()));
        }
        modelTree.setHigherModel(findModelById(modelTreePayload.getHigherModelId()));
        modelTree.setAircraftLocation(aircraftLocationService.findById(modelTreePayload.getLocationId()));
        return modelTree;
    }

    private void hasDuplicateModelTree(ModelTreePayload modelTreePayload, Long id) {
        Optional<Long> existingModelId = modelTreeRepository
                .findIdForUniqueEntry(modelTreePayload.getModelId(), modelTreePayload.getHigherModelId(),
                        modelTreePayload.getLocationId(), modelTreePayload.getPositionId());
        if (!existingModelId.isPresent()) {
            return;
        }
        if (Objects.isNull(id)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.MODEL_TREE_EXISTS_WITH_SIMILAR_CONFIGURATION);
        } else if (!existingModelId.get().equals(id)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.MODEL_TREE_EXISTS_WITH_SIMILAR_CONFIGURATION);
        }
    }


    private void hasReverseModelTree(ModelTreePayload modelTreePayload, Long id) {
        List<Long> modelTreeIds = modelTreeRepository
                .findReverseModelTree(modelTreePayload.getHigherModelId(), modelTreePayload.getModelId());
        if (CollectionUtils.isEmpty(modelTreeIds)) {
            return;
        }
        int size = modelTreeIds.size();
        if (Objects.isNull(id) || size > 1) {
            throw EngineeringManagementServerException.badRequest(ErrorId.REVERSE_MODEL_TREE_EXISTS);
        }

        if (!modelTreeIds.get(0).equals(id)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.REVERSE_MODEL_TREE_EXISTS);
        }
    }

    private Model findModelById(Long modelId) {
        return Objects.nonNull(modelId) ? modelService.findById(modelId) : null;
    }

    /**
     * build specification for given entity
     *
     * @param searchPayload {@link ModelTreeSearchPayload}
     * @return {@link Specification<ModelTree>}
     */
    @Override
    protected Specification<ModelTree> buildSpecification(ModelTreeSearchPayload searchPayload) {
        CustomSpecification<ModelTree> customSpecification = new CustomSpecification<>();
        return Specification
                .where(customSpecification.equalSpecificationAtRoot(searchPayload.getModelId(), MODEL_ID))
                .and(customSpecification.equalSpecificationAtRoot(searchPayload.getHigherModelId(), HIGHER_MODEL_ID))
                .and(customSpecification.equalSpecificationAtRoot(searchPayload.getLocationId(), LOCATION_ID))
                .and(customSpecification.equalSpecificationAtRoot(searchPayload.getPositionId(), POSITION_ID))
                .and(customSpecification.equalSpecificationAtRoot(searchPayload.getIsActive(), IS_ACTIVE));
    }

    private void addModel(ModelTree modelTree, ModelTreeViewModel model) {
        if (Objects.nonNull(modelTree.getModel())) {
            model.setModelName(modelTree.getModel().getModelName());
        }
    }

    private void addHigherModel(ModelTree modelTree, ModelTreeViewModel model) {
        if (Objects.nonNull(modelTree.getHigherModel())) {
            model.setHigherModelName(modelTree.getHigherModel().getModelName());
        }
    }

    private void addLocation(ModelTree modelTree, ModelTreeViewModel model) {
        if (Objects.nonNull(modelTree.getAircraftLocation())) {
            model.setLocationName(modelTree.getAircraftLocation().getName());
        }
    }

    private void addPosition(ModelTree modelTree, ModelTreeViewModel model) {
        if (Objects.nonNull(modelTree.getPosition())) {
            model.setPositionName(modelTree.getPosition().getName());
        }
    }

    /**
     * get entity list
     *
     * @param modelId {@link Long}
     * @return {@link List<ModelTreeViewModel>}
     */
    @Override
    public List<ModelTreeViewModel> getLowerModelList(Long modelId) {
        List<ModelTreeViewModel> modelTreeViewModelList = modelTreeRepository.findAllByHigherModelId(modelId);
        return modelTreeViewModelList
                .stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparingLong(ModelTreeViewModel::getModelId))), ArrayList::new));
    }

    /**
     * Get entity
     *
     * @param higherModelId {@link Long}
     * @param modelId       {@link Long}
     * @return {@link ModelTreeViewModel}
     */
    @Override
    public List<ModelTreeViewModel> findLocationAndPosition(Long higherModelId, Long modelId) {
        if (Objects.isNull(higherModelId) || Objects.isNull(modelId)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ID_IS_REQUIRED);
        }
        return modelTreeRepository.findLocationAndPosition(higherModelId, modelId);
    }

    /**
     * This method is responsible for uploading aircraft location data via Excel file
     *
     * @param file {@link MultipartFile}
     * @return {@link ExcelDataResponse}
     * @throws IOException
     */
    @Override
    @Transactional
    public ExcelDataResponse uploadExcel(MultipartFile file, Long aircraftModelId) {
        if (Objects.isNull(aircraftModelId)) {
            throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_MODEL_ID_REQUIRED);
        }
        ExcelData excelData = ExcelFileUtil.getExcelDataFromSheet(
                file, environment.getProperty(ARM_EXCEL_MODEL_TREE), MODEL_TREE);

        if (CollectionUtils.isNotEmpty(excelData.getErrorMessages())) {
            return ExcelFileUtil.prepareErrorResponse(excelData.getErrorMessages());
        }
        List<String> errorMessage = saveEntity(excelData, aircraftModelId);

        if (CollectionUtils.isNotEmpty(errorMessage)) {
            return ExcelFileUtil.prepareErrorResponse(errorMessage);
        }
        return ExcelFileUtil.prepareSuccessResponse();
    }

    @Override
    public List<PositionDto> getPositionListByModelId(Long modelId) {
        List<Position> positions = modelTreeRepository.getPositionListByModelId(modelId);

        return positions.stream().map(position ->
                PositionDto.builder()
                        .positionId(position.getId())
                        .name(position.getName())
                        .description(position.getDescription())
                        .build()).collect(Collectors.toList());
    }

    @Override
    public List<PositionModelView> getPositionsByModelIds(Set<Long> modelIds) {
        return modelTreeRepository.getPositionListByModelIds(modelIds);
    }


    private List<String> saveEntity(ExcelData excelData, Long aircraftModelId) {
        Set<Model> modelSet = getModelList(aircraftModelId);
        Map<String, Model> modelMap = getModelMap(modelSet);
        Set<Long> higherModelIds = modelSet.stream().map(model -> model.getId()).collect(Collectors.toSet());
        List<ModelTreeViewModel> modelTreeViewModelList = findModelTreeListByHigherModelId(higherModelIds);
        Set<String> duplicateModelTreeKeys = new HashSet<>();
        Set<String> reverseModelTreeKeys = new HashSet<>();
        prepareModelTreeKeys(modelTreeViewModelList, duplicateModelTreeKeys, reverseModelTreeKeys);

        Map<String, AircraftLocation> locationMap = getAircraftLocationMap();
        Map<String, Position> positionMap = getPositionMap();

        List<Map<String, ?>> dataList = excelData.getDataList();
        List<ModelTree> modelTreeList = new ArrayList<>();

        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.EMPTY_LIST;
        }
        List<String> errorMessages = new ArrayList<>();
        for (Map<String, ?> dataMap : dataList) {
            int rowNumber = Integer.valueOf(String.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER)));

            String modelName = StringUtil.valueOf(dataMap.get(MODEL));
            boolean isValid;
            isValid = ExcelFileUtil.addErrorIfKeyNotExists(modelName, MODEL, modelMap, rowNumber, errorMessages);

            String higherModelName = StringUtil.valueOf(dataMap.get(HIGHER_MODEL));
            isValid = isValid & ExcelFileUtil.addErrorIfKeyNotExists(
                    higherModelName, HIGHER_MODEL, modelMap, rowNumber, errorMessages);

            String locationName = StringUtil.valueOf(dataMap.get(LOCATION));
            isValid = isValid & ExcelFileUtil.addErrorIfKeyNotExists(locationName, LOCATION, locationMap, rowNumber, errorMessages);

            String positionName = StringUtil.valueOf(dataMap.get(POSITION));
            if (StringUtils.isNotBlank(positionName) && !positionMap.containsKey(positionName)) {
                errorMessages.add(
                        String.format("Position : {%s} is not present or not active at row : {%s}", positionName, rowNumber));
            }

            if (isValid) {
                Model model = modelMap.get(modelName);
                Model higherModel = modelMap.get(higherModelName);
                AircraftLocation aircraftLocation = locationMap.get(locationName);
                if (model.getId().equals(higherModel.getId())) {
                    errorMessages.add(String.format("Model : {%s} and higherModel : {%s} should " +
                            "be different at row : {%s}", modelName, higherModelName, rowNumber));
                    continue;
                }
                ModelTree modelTree = prepareModelTree(
                        model, higherModel, aircraftLocation, positionMap.get(positionName));
                checkForDuplicateAtDB(modelTree, rowNumber, errorMessages, duplicateModelTreeKeys, reverseModelTreeKeys);
                modelTreeList.add(modelTree);
            }
        }
        if (CollectionUtils.isNotEmpty(errorMessages)) {
            return errorMessages;
        }
        saveItemList(modelTreeList);
        return errorMessages;
    }

    private ModelTree prepareModelTree(Model model, Model higherModel, AircraftLocation location, Position position) {
        ModelTree modelTree = new ModelTree();
        modelTree.setModel(model);
        modelTree.setHigherModel(higherModel);
        modelTree.setAircraftLocation(location);
        modelTree.setPosition(position);
        return modelTree;
    }

    private void prepareModelTreeKeys(List<ModelTreeViewModel> modelTreeViewModelList,
                                      Set<String> duplicateModelTreeKeys, Set<String> reverseModelTreeKeys) {
        if (CollectionUtils.isEmpty(modelTreeViewModelList)) {
            return;
        }
        modelTreeViewModelList.stream().forEach(modelTreeViewModel -> {
            String duplicateModelTreeKey = buildKey(modelTreeViewModel.getModelId(),
                    modelTreeViewModel.getHigherModelId(),
                    modelTreeViewModel.getPositionId(), modelTreeViewModel.getLocationId(), true);
            duplicateModelTreeKeys.add(duplicateModelTreeKey);
            String reverseModelTreeKey = buildKey(modelTreeViewModel.getModelId(),
                    modelTreeViewModel.getHigherModelId(), null, null, false);
            reverseModelTreeKeys.add(reverseModelTreeKey);
        });
    }


    private String buildKey(Long modelId, Long higherModelId, Long positionId, Long locationId, boolean isDuplicate) {
        StringBuilder sb = new StringBuilder().append(Objects.isNull(modelId) ? " " : modelId)
                .append(ApplicationConstant.SEPARATOR)
                .append(Objects.isNull(higherModelId) ? " " : higherModelId);

        if (isDuplicate) {
            sb.append(ApplicationConstant.SEPARATOR).append(Objects.isNull(positionId) ? " " : positionId)
                    .append(ApplicationConstant.SEPARATOR)
                    .append(Objects.isNull(locationId) ? " " : locationId);
        }
        return sb.toString();
    }

    private boolean checkForDuplicateAtDB(ModelTree modelTree, Integer rowNumber, List<String> errorMessages,
                                          Set<String> duplicateModelTreeKeys, Set<String> reverseModelTreeKeys) {
        String duplicateModelTreeKey = buildKey(modelTree.getModel().getId(), modelTree.getHigherModel().getId(),
                Objects.isNull(modelTree.getPosition()) ? null : modelTree.getPosition().getId(), modelTree.getAircraftLocation().getId(), true);
        if (duplicateModelTreeKeys.contains(duplicateModelTreeKey)) {
            errorMessages.add(String.format("Model tree exists with same configuration." +
                            " for model : {%s}, higherModel : {%s} position : {%s} location: {%s} at row : {%s}",
                    modelTree.getModel().getModelName(), modelTree.getHigherModel().getModelName(),
                    Objects.isNull(modelTree.getPosition()) ? " " : modelTree.getPosition().getName(),
                    modelTree.getAircraftLocation().getName(), rowNumber));
        }
        String reverseModelTreeKey = buildKey(modelTree.getHigherModel().getId(),
                modelTree.getModel().getId(), null, null, false);
        if (reverseModelTreeKeys.contains(reverseModelTreeKey)) {
            errorMessages.add(String.format("Reverse model tree exists with this model : {%s} and higher model : {%s} at row: {%s}",
                    modelTree.getModel().getModelName(), modelTree.getHigherModel().getModelName(), rowNumber));
        }
        return true;
    }

    private Map<String, Position> getPositionMap() {

        Set<Position> positionSet = positionService.findAllActivePosition();
        if (CollectionUtils.isEmpty(positionSet)) {
            return Collections.EMPTY_MAP;
        }
        return positionSet.stream()
                .collect(Collectors.toMap(position -> position.getName(), Function.identity()));
    }

    private Set<Model> getModelList(Long aircraftModelId) {
        return modelService.findAllByAircraftModelId(aircraftModelId);
    }

    private Map<String, Model> getModelMap(Set<Model> modelSet) {
        if (CollectionUtils.isEmpty(modelSet)) {
            return Collections.EMPTY_MAP;
        }
        return modelSet.stream()
                .collect(Collectors.toMap(modelResponseDto -> modelResponseDto.getModelName(), Function.identity()));
    }

    private Map<String, AircraftLocation> getAircraftLocationMap() {
        Set<AircraftLocation> aircraftLocationDtoSet = aircraftLocationService.findAllActiveAircraftLocation();
        if (CollectionUtils.isEmpty(aircraftLocationDtoSet)) {
            return Collections.EMPTY_MAP;
        }
        return aircraftLocationDtoSet.stream()
                .collect(Collectors.toMap(modelResponseDto -> modelResponseDto.getName(), Function.identity()));
    }

    private List<ModelTreeViewModel> findModelTreeListByHigherModelId(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return modelTreeRepository
                .findAllByHigherModelIdIn(ids, PageRequest.of(0, ApplicationConstant.MAX_LIMIT)).getContent();
    }

    @Override
    public List<ModelTree> findAllModelTreeByAircraftId(Set<Long> modelIds) {
        if (Objects.isNull(modelIds)) {
            return Collections.emptyList();
        }
        return modelTreeRepository
                .findAllModelTreeByIdIn(modelIds, PageRequest.of(0, ApplicationConstant.MAX_LIMIT)).getContent();
    }

    @Override
    public Optional<Long> findIdForUniqueEntry(Long modelId, Long higherModelId, Long locationId, Long positionId) {
        return modelTreeRepository.findIdForUniqueEntry(modelId, higherModelId, locationId, positionId);
    }

    @Override
    public List<ModelTreeExcelViewModel> getAllModelTreeList() {
        return modelTreeRepository.findAllModelTree();
    }

}
