package com.digigate.engineeringmanagement.common.controller;

import com.digigate.engineeringmanagement.common.service.ErpDataSyncService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/erp/sync")
public class ErpDataSyncController {

    private final ErpDataSyncService erpDataSyncService;

    public ErpDataSyncController(ErpDataSyncService erpDataSyncService) {
        this.erpDataSyncService = erpDataSyncService;
    }

    @GetMapping
    public ResponseEntity<?> sync(@RequestParam(value = "all", required = false, defaultValue = "False") Boolean all) {
        erpDataSyncService.sync(all);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
