package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.planning.entity.Defect;
import com.digigate.engineeringmanagement.planning.payload.request.DefectDto;
import com.digigate.engineeringmanagement.planning.payload.request.DefectSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.DefRectSearchViewModel;
import com.digigate.engineeringmanagement.planning.service.DefectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * Defect Controller
 */
@RestController
@RequestMapping("/api/defect")
public class DefectController extends AbstractController<Defect, DefectDto> {

    private static final String BULK_DATA_SAVED_SUCCESSFULLY = "Created Successfully Bulk Defects";
    private final DefectService defectService;

    @Autowired
    public DefectController(AbstractService<Defect, DefectDto> defectService, DefectService defectService1) {
        super(defectService);
        this.defectService = defectService1;
    }


    @PostMapping("/generate/{aircraftId}")
    public ResponseEntity<List<DefectDto>> getGeneratedDefectList(
            @Valid @RequestBody List<DefRectSearchViewModel> generatedDefects, @PathVariable Long aircraftId) {
        return new ResponseEntity<>(defectService.getGeneratedDefectList(generatedDefects, aircraftId), HttpStatus.OK);
    }

    @PostMapping("/bulk")
    public ResponseEntity<MessageResponse> createDefectBulk(@Valid @RequestBody List<DefectDto> defectDtoList) {
        defectService.createDefectBulk(defectDtoList);
        return ResponseEntity.ok(new MessageResponse(BULK_DATA_SAVED_SUCCESSFULLY));
    }

    @PostMapping("/search")
    public ResponseEntity<PageData> search(@RequestBody @Valid DefectSearchDto searchDto, Pageable pageable) {
        return new ResponseEntity<>(defectService.searchDefects(searchDto, pageable), HttpStatus.OK);
    }

    @PostMapping("/top-ata-report")
    public ResponseEntity<?> findTopAtaReport(
            @RequestBody @Valid DefectSearchDto searchDto,
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        Pageable pageable;
        if (Objects.isNull(pageNo) || Objects.isNull(pageSize)) {
            pageable = PageRequest.of(0, 10);
        } else {
            pageable = PageRequest.of(pageNo, pageSize);
        }
        return new ResponseEntity<>(defectService.findTopAtaReport(searchDto, pageable),
                HttpStatus.OK);
    }

    @PostMapping("/crr-report")
    public ResponseEntity<?> crrReport(
            @RequestBody @Valid DefectSearchDto searchDto,
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        Pageable pageable;
        if (Objects.isNull(pageNo) || Objects.isNull(pageSize)) {
            pageable = PageRequest.of(0, 10);
        } else {
            pageable = PageRequest.of(pageNo, pageSize);
        }
        return new ResponseEntity<>(defectService.findCrrReport(searchDto, pageable),
                HttpStatus.OK);
    }
}
