package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.config.model.ExcelDataResponse;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.dto.request.SerialPartSearchDto;
import com.digigate.engineeringmanagement.planning.dto.request.SerialRequestDto;
import com.digigate.engineeringmanagement.planning.entity.Serial;
import com.digigate.engineeringmanagement.planning.payload.response.SerialListViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.SerialResponseView;
import com.digigate.engineeringmanagement.planning.payload.response.SerialViewModel;
import com.digigate.engineeringmanagement.planning.service.SerialService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/serials")
public class SerialController extends AbstractController<Serial, SerialRequestDto> {

    private final SerialService serialService;
    public SerialController(SerialService serialService) {
        super(serialService);
        this.serialService = serialService;
    }

    @GetMapping("/serial-by-part")
    public ResponseEntity<List<SerialResponseView>> getSerialListByPartId(@RequestParam("partId") Long partId) {
        return new ResponseEntity<>(serialService.getSerialListByPartId(partId), HttpStatus.OK);
    }

    /**
     * This is an API endpoint for uploading Serial from Excel file
     *
     * @param file {@link  MultipartFile}
     * @return {@link ResponseEntity < ExcelDataResponse >}
     */
    @PostMapping("/upload")
    public ResponseEntity<ExcelDataResponse> importExcel(@RequestParam("file") MultipartFile file,
                                                         @RequestParam("aircraftModelId") Long aircraftModelId) {
        ExcelDataResponse excelDataResponse = serialService.uploadExcel(file, aircraftModelId);
        return new ResponseEntity<>(excelDataResponse, excelDataResponse.getStatus());
    }

    @PostMapping("/search")
    public ResponseEntity<PageData> search(@RequestBody SerialPartSearchDto serialPartSearchDto, @PageableDefault(
            sort = ApplicationConstant.DEFAULT_SORT,
            direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(serialService.searchBySerialNo(serialPartSearchDto, pageable), HttpStatus.OK);
    }

    @PostMapping("/search-by-part-id")
    public ResponseEntity<PageData> searchSerialNoByPartId(@RequestBody SerialPartSearchDto serialPartSearchDto,
                                                           @PageableDefault(
                                                                   sort = ApplicationConstant.DEFAULT_SORT,
                                                                   direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(serialService.searchSerialNoByPartId(serialPartSearchDto, pageable), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<SerialListViewModel>> getSerialList() {
        return new ResponseEntity<>(serialService.findAllSerialByList(), HttpStatus.OK);
    }
}
