package com.digigate.engineeringmanagement.planning.service.impl;

import com.digigate.engineeringmanagement.common.config.model.ExcelData;
import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.config.util.ExcelFileUtil;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.aircraftinformation.AircraftDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import com.digigate.engineeringmanagement.configurationmanagement.entity.AircraftModel;
import com.digigate.engineeringmanagement.configurationmanagement.repository.aircraftinformation.AircraftModelRepository;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftModelService;
import com.digigate.engineeringmanagement.planning.constant.LifeCodes;
import com.digigate.engineeringmanagement.planning.constant.ModelConstant;
import com.digigate.engineeringmanagement.planning.constant.ModelType;
import com.digigate.engineeringmanagement.planning.entity.Model;
import com.digigate.engineeringmanagement.planning.payload.request.ModelDto;
import com.digigate.engineeringmanagement.planning.payload.request.ModelSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.ModelExcelResponseDto;
import com.digigate.engineeringmanagement.planning.payload.response.ModelResponseByAircraftDto;
import com.digigate.engineeringmanagement.planning.payload.response.ModelResponseDto;
import com.digigate.engineeringmanagement.planning.repository.ModelRepository;
import com.digigate.engineeringmanagement.planning.service.IModelService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;

/**
 * Model service implementation
 *
 * @author Asifuf Rahman
 */
@Service
public class ModelService extends AbstractSearchService<Model, ModelDto, ModelSearchDto> implements IModelService {
    private final ModelRepository modelRepository;
    private final AircraftModelService aircraftModelService;
    private final IService<Aircraft, AircraftDto> aircraftService;
    private final AircraftModelRepository aircraftModelRepository;
    private final Environment environment;


    /**
     * Autowired constructor
     * @param modelRepository      {@link ModelRepository}
     * @param aircraftModelService {@link AircraftModelService}
     * @param aircraftService
     * @param aircraftModelRepository
     * @param environment
     */
    @Autowired
    public ModelService(ModelRepository modelRepository, AircraftModelService aircraftModelService,
                        IService<Aircraft, AircraftDto> aircraftService,
                        AircraftModelRepository aircraftModelRepository, Environment environment) {

        super(modelRepository);
        this.modelRepository = modelRepository;
        this.aircraftModelService = aircraftModelService;
        this.aircraftService = aircraftService;
        this.aircraftModelRepository = aircraftModelRepository;
        this.environment = environment;
    }

    /**
     * responsible for create Model
     *
     * @param modelDto {@link ModelDto}
     * @return Model entity
     */
    @Override
    public Model create(ModelDto modelDto) {
        Optional<Model> model = modelRepository.findByModelName(modelDto.getModelName());
        if (model.isPresent()) {
            throw new EngineeringManagementServerException(ErrorId.MODEL_NAME_ALREADY_EXIST, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return saveItem(convertToEntity(modelDto));
    }

    /**
     * responsible for updating Model information
     *
     * @param modelDto {@link ModelDto}
     * @param id       id of Model
     * @return Model entity
     */
    @Override
    public Model update(ModelDto modelDto, Long id) {
        Optional<Model> model = modelRepository.findByModelNameAndIdNot(modelDto.getModelName(), id);
        if (model.isPresent()) {
            throw new EngineeringManagementServerException(ErrorId.MODEL_NAME_ALREADY_EXIST, HttpStatus.BAD_REQUEST,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return saveItem(updateEntity(modelDto, findByIdUnfiltered(id)));
    }

    /**
     * mapper entity to responseDto
     *
     * @param model {@link Model}
     * @return {@link ModelResponseDto}
     */
    @Override
    protected ModelResponseDto convertToResponseDto(Model model) {
        AircraftModel aircraftModel = aircraftModelService.findById(model.getAircraftModel().getId());
        Set<String> lifeCodes = new HashSet<>();
        model.getLifeCodes().forEach(val -> {
            lifeCodes.add(LifeCodes.getName(val));
        });
        return ModelResponseDto.builder()
                .modelId(model.getId())
                .modelName(model.getModelName())
                .modelType(model.getModelType())
                .description(model.getDescription())
                .lifeCodes(model.getLifeCodes())
                .lifeCodesValue(lifeCodes)
                .version(model.getVersion())
                .aircraftModelId(aircraftModel.getId())
                .aircraftModelName(aircraftModel.getAircraftModelName())
                .isActive(model.getIsActive())
                .build();
    }

    @Override
    protected Model convertToEntity(ModelDto dto) {
        return mapToEntity(new Model(), dto);
    }

    @Override
    protected Model updateEntity(ModelDto dto, Model model) {
        return mapToEntity(model, dto);
    }

    /**
     * Conversion of dto to entity
     *
     * @param entity {@link Model}
     * @param dto    {@link ModelDto}
     * @return entity {@link Model}
     */
    protected Model mapToEntity(Model entity, ModelDto dto) {
        entity.setAircraftModel(aircraftModelService.findById(dto.getAircraftModelId()));
        entity.setModelName(dto.getModelName());
        entity.setDescription(dto.getDescription());
        entity.setVersion(dto.getVersion());
        entity.setModelType(dto.getModelType());
        if (Objects.nonNull(dto.getLifeCodes())) {
            entity.setLifeCodes(dto.getLifeCodes());
        }
        return entity;
    }



    /**
     * Search-service of Model
     *
     * @param searchDto {@link ModelSearchDto}
     * @return entity {@link Specification<Model>}
     */
    @Override
    protected Specification<Model> buildSpecification(ModelSearchDto searchDto) {
        CustomSpecification<Model> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.likeSpecificationAtPrefixAndSuffix(searchDto.getModelName(),
                        ModelConstant.MODEL_NAME))
                .and(customSpecification.equalSpecificationAtChild(searchDto.getAircraftModelId(),
                        ModelConstant.AIRCRAFT_MODEL_TABLE_NAME, ModelConstant.AIRCRAFT_MODEL_ID))
                .and(customSpecification.equalSpecificationAtRoot(searchDto.getIsActive(), ModelConstant.IS_ACTIVE));
    }

    /**
     * Get Model List By Aircraft model id
     *
     * @param aircraftId {@value  <Long>}
     * @return list of models as view model
     */
    @Override
    public List<ModelResponseByAircraftDto> getModelListByAircraft(Long aircraftId) {
        return modelRepository
                .findAllByAircraftModelId(aircraftModelService.findAircraftModelIdByAircraftId(aircraftId));
    }

    /**
     * Get Model List By Aircraft model id
     *
     * @param aircraftModelId {@value  <Long>}
     * @return list of models as view model
     */
    @Override
    public List<ModelResponseByAircraftDto> getModelListByAircraftId(Long aircraftModelId) {
        return modelRepository.findAllByAircraftModelId(aircraftModelId);
    }

    @Override
    public List<ModelResponseByAircraftDto> getConsumableModelByAircraftModelId(Long aircraftModelId) {
        return modelRepository.findModelByAircraftModelAAndModelType(aircraftModelId, ModelType.CONSUMABLE_MODEL);
    }


    @Override
    public ExcelDataResponse uploadExcel(MultipartFile file, Long aircraftModelId) {
        if(Objects.isNull(aircraftModelId)){
            throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_MODEL_ID_REQUIRED);
        }

        ExcelData excelData = ExcelFileUtil
                .getExcelDataFromSheet(file, environment.getProperty(ModelConstant.ARM_EXCEL_MODEL)
                        ,ModelConstant.MODEL);

        if(CollectionUtils.isNotEmpty(excelData.getErrorMessages())){
            return ExcelFileUtil.prepareErrorResponse(excelData.getErrorMessages());
        }

        List<String> errorMessage = validateAndPrepareEntity(excelData,aircraftModelId);

        if(CollectionUtils.isNotEmpty(errorMessage)){
            return ExcelFileUtil.prepareErrorResponse(errorMessage);
        }

        return ExcelFileUtil.prepareSuccessResponse();
    }

    private List<String> validateAndPrepareEntity(ExcelData excelData, Long aircraftModelId) {
        List<String> errorMessages = new ArrayList<>();
        List<Model> modelList = new ArrayList<>();
        if (CollectionUtils.isEmpty(excelData.getDataList())) {
            return Collections.emptyList();
        }

        Set<String>modelNamesInDb = modelRepository.findModelNamesByAircraftModelId(aircraftModelId);
        Optional<AircraftModel> aircraftModelOptional = aircraftModelRepository.findNameByAircraftId(aircraftModelId);

        if(aircraftModelOptional.isEmpty()){
            throw EngineeringManagementServerException.badRequest(ErrorId.AIRCRAFT_MODEL_NOT_FOUND_WITH_ID);
        }

        AircraftModel aircraftModel = aircraftModelOptional.get();


        List<Map<String, ?>> dataList = excelData.getDataList();
        for (Map<String, ?> dataMap : dataList) {
            int rowNumber = Integer.valueOf(String.valueOf(dataMap.get(ApplicationConstant.ROW_NUMBER)));
            ModelType modelType = (ModelType) dataMap.get(ModelConstant.MODEL_TYPE);
            String modelName = (String)dataMap.get(ModelConstant.MODEL);
            List<LifeCodes> lifeCodes = ( List<LifeCodes> ) dataMap.get(ModelConstant.LIFE_CODES);

            boolean isValid = true;
            if(modelNamesInDb.contains(modelName)){
                errorMessages.add(String.format("Model: {%s} already exists in database at row: {%s}"
                        ,modelName, rowNumber));
                isValid = false;
            }


            Set<Integer>lifeCodeSet = new HashSet<>();


            if( CollectionUtils.isNotEmpty(lifeCodes) ){
                for(LifeCodes lc: lifeCodes){
                    if(Objects.isNull(lc)){
                        errorMessages.add(String.format("Life Code Enum error at row: %s",rowNumber));
                        isValid = false;
                        break;
                    }
                    lifeCodeSet.add(lc.val);
                }
            }

            if(isValid){
                modelList.add(prepareModel(
                        aircraftModel,
                        modelName,
                        (String)dataMap.get(ModelConstant.MODEL_DESCRIPTION),
                        (String) dataMap.get(ModelConstant.MODEL_VERSION),
                        modelType,
                        lifeCodeSet
                ));
            }

        }

        if(CollectionUtils.isEmpty(errorMessages) && CollectionUtils.isNotEmpty(modelList)){
            saveItemList(modelList);
        }


        return errorMessages;
    }

    private Model prepareModel(AircraftModel aircraftModel, String modelName, String description , String version,
                               ModelType modelType, Set<Integer>lifeCodes){
        Model model = new Model();
        model.setAircraftModel(aircraftModel);
        model.setModelName(modelName);
        model.setDescription(description);
        model.setVersion(version);
        model.setModelType(modelType);
        model.setLifeCodes(lifeCodes);
        return model;
    }


    public Set<Model> findAllByAircraftModelId(Long aircraftModelId) {
        return modelRepository.findAllByAircraftModelIdAndIsActiveTrue(aircraftModelId);
    }

    /**
     * Get Model ids by aircraftModelId
     *
     * @param aircraftId {@link Long}
     * @return list of model ids
     */
    @Override
    public Set<Long> findModelIdsByAircraftId(Long aircraftId) {
        return modelRepository
                .findModelIdsByAircraftModelId(aircraftModelService.findAircraftModelIdByAircraftId(aircraftId));
    }

    @Override
    public List<Model> findAllModelByAircraftModelId(Long aircraftModelId) {
        return modelRepository.findAllModelByAircraftModelId(aircraftModelId);
    }

    @Override
    public List<ModelExcelResponseDto> getAllModelList() {
        return modelRepository.findAllActiveModel();
    }

}
