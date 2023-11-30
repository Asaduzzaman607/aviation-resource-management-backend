package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.Position;
import com.digigate.engineeringmanagement.planning.payload.request.PositionDto;
import com.digigate.engineeringmanagement.planning.payload.request.PositionSearchDto;
import com.digigate.engineeringmanagement.planning.service.PositionIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/position")
public class PositionController extends AbstractSearchController<Position, PositionDto, PositionSearchDto> {
    private final PositionIService positionIService;
    /**
     * * @param service                {@link IService}
     * @Param positionIService         {@link  PositionIService}
     */
    public PositionController(ISearchService<Position, PositionDto, PositionSearchDto> iSearchService, PositionIService positionIService) {
        super(iSearchService);
        this.positionIService = positionIService;
    }

    /**
     * This is an API endpoint for uploading Positions from excel file
     *
     * @param file {@link  MultipartFile}
     * @return {@link ResponseEntity<ExcelDataResponse>}
     */
    @PostMapping("/upload")
    public ResponseEntity<ExcelDataResponse> importExcel(@RequestParam("file") MultipartFile file) {
        ExcelDataResponse excelDataResponse = positionIService.uploadExcel(file);
        return new ResponseEntity<>(excelDataResponse, excelDataResponse.getStatus());
    }
}
