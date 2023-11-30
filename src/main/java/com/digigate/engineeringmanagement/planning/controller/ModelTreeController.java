package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.ModelTree;
import com.digigate.engineeringmanagement.planning.entity.Position;
import com.digigate.engineeringmanagement.planning.payload.request.ModelTreePayload;
import com.digigate.engineeringmanagement.planning.payload.request.ModelTreeSearchPayload;
import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.planning.payload.request.PositionDto;
import com.digigate.engineeringmanagement.planning.payload.response.ModelTreeExcelViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.ModelTreeViewModel;
import com.digigate.engineeringmanagement.planning.service.ModelTreeIService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * ModelTree Controller
 *
 * @author Masud Rana
 */
@RestController
@RequestMapping("/api/model-tree")
public class ModelTreeController extends AbstractSearchController<ModelTree, ModelTreePayload, ModelTreeSearchPayload> {
    private final ModelTreeIService modelTreeIService;

    /**
     * Parameterized constructor
     *
     * @param service           {@link IService}
     * @param modelTreeIService {@link ModelTreeIService}
     */
    public ModelTreeController(ISearchService<ModelTree, ModelTreePayload, ModelTreeSearchPayload> service,
                               ModelTreeIService modelTreeIService) {
        super(service);
        this.modelTreeIService = modelTreeIService;
    }

    /**
     * Get all lower model
     *
     * @param id {@link Long}
     * @return {@link List<ModelTreeViewModel>}
     */
    @GetMapping("/model/{id}")
    public ResponseEntity<List<ModelTreeViewModel>> getAllLowerModel(@PathVariable Long id) {
        return new ResponseEntity<>(modelTreeIService.getLowerModelList(id), HttpStatus.OK);
    }

    /**
     * Get entity
     *
     * @param higherModelId {@link Long}
     * @param id            {@link Long}
     * @return {@link ModelTreeViewModel}
     */
    @GetMapping("/higher-model/{higherModelId}/model/{id}")
    public ResponseEntity<List<ModelTreeViewModel>> findLocationAndPosition(
            @PathVariable Long higherModelId, @PathVariable Long id) {
        return new ResponseEntity<>(modelTreeIService.findLocationAndPosition(higherModelId, id), HttpStatus.OK);
    }

    /**
     * Upload excel
     *
     * @param file {@link MultipartFile}
     * @return {@link  ResponseEntity<  ExcelDataResponse  >}
     * @throws IOException
     */
    @PostMapping("/upload/{aircraftModelId}")
    public ResponseEntity<ExcelDataResponse> importExcelFile(
            @RequestParam("file") MultipartFile file, @PathVariable Long aircraftModelId) {
        ExcelDataResponse excelDataResponse = modelTreeIService.uploadExcel(file, aircraftModelId);
        return new ResponseEntity<>(excelDataResponse, excelDataResponse.getStatus());
    }


    /**
     * get position list by model id
     *
     * @param modelId {@link Long}
     * @return {@link  List<PositionDto>}
     */
    @GetMapping("/position-by-model/{modelId}")
    public ResponseEntity<List<PositionDto>> getPositionListByModelId(@PathVariable Long modelId) {
        return new ResponseEntity<>(modelTreeIService.getPositionListByModelId(modelId), HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ModelTreeExcelViewModel>> getAllModelTreeList(){
        return new ResponseEntity<>(modelTreeIService.getAllModelTreeList(),HttpStatus.OK);
    }

}
