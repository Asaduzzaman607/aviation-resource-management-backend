package com.digigate.engineeringmanagement.storemanagement.controller.storedemand;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartAvailabilityLog;
import com.digigate.engineeringmanagement.storemanagement.fileupload.InventoryReportService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartAvailabilityLogRequestDto;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartAvailabilityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/part-availabilities-log")
public class StorePartAvailabilityLogController extends AbstractController<StorePartAvailabilityLog,
        StorePartAvailabilityLogRequestDto> {
    private final InventoryReportService inventoryReportService;

    public StorePartAvailabilityLogController(StorePartAvailabilityLogService storePartAvailabilityLogService,
                                              InventoryReportService inventoryReportService) {
        super(storePartAvailabilityLogService);
        this.inventoryReportService = inventoryReportService;
    }

    /**
     * Upload excel
     *
     * @param file            {@link  MultipartFile}
     * @return {@link ResponseEntity < ExcelDataResponse > }
     */
    @PostMapping("/file-upload")
    public ResponseEntity<?> importExcelFile(
            @RequestParam(value = "aircraftModelId", required = false, defaultValue = "0")Long aircraftModelId,
            @RequestParam("file") MultipartFile file,
            @RequestParam List<String> sheetNameList) {
        return ResponseEntity.ok(inventoryReportService.uploadExcel(file, sheetNameList,aircraftModelId));
    }
}
