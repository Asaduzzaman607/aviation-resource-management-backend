package com.digigate.engineeringmanagement.storemanagement.controller.partsreceive;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.partsreceive.StoreStockInward;
import com.digigate.engineeringmanagement.storemanagement.payload.request.partsreceive.StoreStockInwardDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.PartSerialsViewModel;
import com.digigate.engineeringmanagement.storemanagement.service.partsreceive.StoreStockInwardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/store/stock-inwards")
public class StoreStockInwardController extends AbstractSearchController<StoreStockInward, StoreStockInwardDto, IdQuerySearchDto> {
    private final StoreStockInwardService storeStockInwardService;

    public StoreStockInwardController(StoreStockInwardService storeStockInwardService) {
        super(storeStockInwardService);
        this.storeStockInwardService = storeStockInwardService;
    }

    @GetMapping("/poi/{id}")
    public ResponseEntity<List<PartSerialsViewModel>> getPartsFromPartOrder(@PathVariable("id") Long inwardId){
        return ResponseEntity.ok(storeStockInwardService.getPartsFromPartOrder(inwardId));
    }
}
