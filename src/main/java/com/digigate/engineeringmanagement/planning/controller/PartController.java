package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.dto.request.PartSearchDto;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.payload.request.PartDto;
import com.digigate.engineeringmanagement.planning.payload.response.*;
import com.digigate.engineeringmanagement.planning.service.PartService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Part controller
 *
 * @author ashinisingha
 */
@RestController
@RequestMapping("/api/part")
public class PartController extends AbstractController<Part, PartDto> {

    private final PartService partService;

    /**
     * parameterized constructor
     *  @param service           {@link IService}
     * @param partService        {@link  PartService}
     */
    public PartController(IService<Part, PartDto> service, PartService partService) {
        super(service);
        this.partService = partService;
    }

    /**
     * This is an API endpoint to getting parts by model id
     *
     * @param id                   {@link  Long}
     * @return                     {@link  ResponseEntity}
     */
    @GetMapping("/model/{id}")
    public ResponseEntity<List<PartViewModelLite>> findByModelId(@PathVariable Long id){
        return ResponseEntity.ok(partService.findAllByModelId(id));
    }

    /**
     * API endpoint for searching Part
     *
     * @param partSearchDto {@link PartSearchDto}
     * @param pageable      {@link Pageable}
     * @return              data
     */
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody PartSearchDto partSearchDto, @PageableDefault(
            sort = ApplicationConstant.DEFAULT_SORT,
            direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(partService.search(partSearchDto, pageable), HttpStatus.OK);
    }

    @PostMapping("/common-part")
    public ResponseEntity<?> commonPartSearch(@RequestBody PartSearchDto partSearchDto,
                                              @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                      direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(partService.searchCommonPart(pageable, partSearchDto), HttpStatus.OK);
    }

    @PostMapping("/search-by-part-and-ac-type")
    public ResponseEntity<?> searchByPartAndAcType(@RequestBody PartSearchDto partSearchDto,
                                              @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                      direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(partService.searchByPartAndAcType(pageable, partSearchDto), HttpStatus.OK);
    }

    /**
     * Upload excel
     *
     * @param file            {@link  MultipartFile}
     * @param aircraftModelId {@link  Long}
     * @return {@link ResponseEntity<ExcelDataResponse> }
     */
    @PostMapping("/upload/{aircraftModelId}")
    public ResponseEntity<ExcelDataResponse> importExcelFile(
            @RequestParam("file") MultipartFile file, @PathVariable Long aircraftModelId) {
        ExcelDataResponse excelDataResponse = partService.uploadExcel(file, aircraftModelId);
        return new ResponseEntity<>(excelDataResponse, excelDataResponse.getStatus());
    }

    @GetMapping("/find-all-consumable-part")
    public ResponseEntity<List<PartViewModel>> findAllConsumablePart() {
        return new ResponseEntity<>(partService.findAllConsumablePart(), HttpStatus.OK);
    }

    @GetMapping("/search-by-part-no")
    public ResponseEntity<List<PartViewModelLite>> searchByPartNo(@RequestParam("partNo") String partNo) {
        return new ResponseEntity<>(partService.searchByPartNo(partNo), HttpStatus.OK);
    }

    @GetMapping("/part-by-aircraft")
    public ResponseEntity<List<PartViewModelLite>> getPartListByAcTypeOfAircraftId(
            @RequestParam("aircraftId") Long aircraftId) {
        return new ResponseEntity<>(partService.getPartListByAcTypeOfAircraftId(aircraftId), HttpStatus.OK);
    }

    @PostMapping("/dashboard/demand")
    public ResponseEntity<?> searchPartDetailsWithDemand(@RequestBody PartSearchDto partSearchDto,
                                                         @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                                 direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(partService.searchPartDetailsWithDemand(partSearchDto, pageable), HttpStatus.OK);
    }

    @PostMapping("/dashboard/issue")
    public ResponseEntity<?> searchPartDetailsWithIssue(@RequestBody PartSearchDto partSearchDto,
                                                        @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                                direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(partService.searchPartDetailsWithIssue(partSearchDto, pageable), HttpStatus.OK);
    }

    @PostMapping("/dashboard/scrap")
    public ResponseEntity<?> searchPartDetailsWithScrap(@RequestBody PartSearchDto partSearchDto,
                                                        @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                                direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(partService.searchPartDetailsWithScrap(partSearchDto, pageable), HttpStatus.OK);
    }

    @PostMapping("/dashboard/requisition")
    public ResponseEntity<?> searchPartDetailsWithRequisition(@RequestBody PartSearchDto partSearchDto,
                                                        @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                                direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(partService.searchPartDetailsWithRequisition(partSearchDto, pageable), HttpStatus.OK);
    }

    @PostMapping("/dashboard/availability")
    public ResponseEntity<?> searchPartDetailsWithAvailability(@RequestBody PartSearchDto partSearchDto,
                                                        @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT,
                                                                direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(partService.searchPartDetailsWithAvailability(partSearchDto, pageable), HttpStatus.OK);
    }

    @PostMapping("/dashboard/stock-card")
    public ResponseEntity<StockCardVM> searchDataForStockCard(@RequestBody PartSearchDto partSearchDto) {
        return new ResponseEntity<>(partService.findDataForStockCard(partSearchDto), HttpStatus.OK);
    }

    @PostMapping("/dashboard/bin-card")
    public ResponseEntity<BinCardVM> findDataForBinCard(@RequestBody PartSearchDto partSearchDto) {
        return new ResponseEntity<>(partService.findDataForBinCard(partSearchDto), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<PartListViewModel>> getPartList() {
        return new ResponseEntity<>(partService.findAllPartByList(), HttpStatus.OK);
    }
}
