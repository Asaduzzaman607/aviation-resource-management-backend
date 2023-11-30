package com.digigate.engineeringmanagement.storemanagement.controller.storedemand;


import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreReturnPart;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreReturnPartRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand.RpdiViewModel;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ReturnPartsDetailService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreReturnPartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/return-store-parts")
public class StoreReturnPartController extends AbstractSearchController<StoreReturnPart, StoreReturnPartRequestDto, IdQuerySearchDto> {
    private final ReturnPartsDetailService returnPartsDetailService;

    public StoreReturnPartController(StoreReturnPartService storeReturnPartService,
                                     ReturnPartsDetailService returnPartsDetailService) {
        super(storeReturnPartService);
        this.returnPartsDetailService = returnPartsDetailService;
    }

    @GetMapping("/inspection/{id}")
    public ResponseEntity<RpdiViewModel> getInspection(@PathVariable Long id) {
        return ResponseEntity.ok(returnPartsDetailService.findInspectionByPartSerialId(id));
    }
}