package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.planning.entity.Model;
import com.digigate.engineeringmanagement.planning.payload.request.ModelDto;
import com.digigate.engineeringmanagement.planning.payload.request.ModelSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.ModelExcelResponseDto;
import com.digigate.engineeringmanagement.planning.payload.response.ModelResponseByAircraftDto;
import com.digigate.engineeringmanagement.planning.payload.response.ModelResponseDto;
import com.digigate.engineeringmanagement.planning.service.ModelIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.digigate.engineeringmanagement.planning.service.IModelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Model Controller
 *
 * @author Asifur Rahman
 */
@RestController
@RequestMapping("/api/model")
public class ModelController extends AbstractSearchController<Model, ModelDto, ModelSearchDto> {


    private final IModelService modelService;

    /**
     * Parameterized constructor
     *
     * @param modelService {@link ModelIService}
     * @param iSearchService {@link ISearchService<Model,ModelDto,ModelSearchDto>}
     */
    public ModelController(IModelService modelService,
                           ISearchService<Model, ModelDto, ModelSearchDto> iSearchService) {
        super(iSearchService);
        this.modelService = modelService;
    }

    /**
     * Get Model List By Aircraft
     *
     * @param aircraftId {@value  <Long>}
     * @return dtos {@link ModelResponseByAircraftDto}
     */
    @GetMapping("/aircraft/{aircraftId}")
    public List<ModelResponseByAircraftDto> getModelListByAircraft(@PathVariable Long aircraftId) {
        return modelService.getModelListByAircraft(aircraftId);
    }

    /**
     * Get Model List By Aircraft
     *
     * @param aircraftModelId {@value  <Long>}
     * @return dtos {@link ModelResponseByAircraftDto}
     */
    @GetMapping("/aircraftModel/{aircraftModelId}")
    public ResponseEntity<List<ModelResponseByAircraftDto>>
    getModelListByAircraftModelId(@PathVariable Long aircraftModelId) {
        return ResponseEntity.ok(modelService.getModelListByAircraftId(aircraftModelId));
    }


    /**
     * Get Consumable Model List By Aircraft
     *
     * @param aircraftModelId {@value  <Long>}
     * @return dtos {@link ModelResponseByAircraftDto}
     */
    @GetMapping("/consumableModel/{aircraftModelId}")
    public ResponseEntity<List<ModelResponseByAircraftDto>>
    getConsumableModelByAcModel(@PathVariable Long aircraftModelId) {
        return ResponseEntity.ok(modelService.getConsumableModelByAircraftModelId(aircraftModelId));
    }
    /**
     * This is an api endpoint to upload model via excel file
     *
     * @param file {@link MultipartFile}
     * @param aircraftModelId {@link Long}
     * @return {@link ResponseEntity<ExcelDataResponse>}
     */
    @PostMapping("/upload/{aircraftModelId}")
    public ResponseEntity<ExcelDataResponse> importExcel(@RequestParam("file") MultipartFile file,
                                                         @PathVariable Long aircraftModelId) {
        ExcelDataResponse excelDataResponse = modelService.uploadExcel(file, aircraftModelId);
        return new ResponseEntity<>(excelDataResponse, excelDataResponse.getStatus());
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ModelExcelResponseDto>> getAllModelList(){
        return ResponseEntity.ok(modelService.getAllModelList());
    }

}
