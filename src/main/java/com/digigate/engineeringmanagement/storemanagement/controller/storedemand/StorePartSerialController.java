package com.digigate.engineeringmanagement.storemanagement.controller.storedemand;


import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.PartSerialSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartSerialRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.UnserviceableComponentListViewModel;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartSerialService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/store_part_serial")
public class StorePartSerialController extends AbstractSearchController<StorePartSerial, StorePartSerialRequestDto, PartSerialSearchDto> {
    private final StorePartSerialService storePartSerialService;

    public StorePartSerialController(StorePartSerialService storePartSerialService) {
        super(storePartSerialService);
        this.storePartSerialService = storePartSerialService;
    }

    @PostMapping("/unserviceableComponentList")
    public ResponseEntity<PageData> getAllUnserviceableComponentList(
            @PageableDefault(sort = ApplicationConstant.DEFAULT_SORT, direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(storePartSerialService.getAllUnserviceableComponentList(pageable), HttpStatus.OK);
    }

    @GetMapping("/unserviceableComponentList")
    public ResponseEntity<List<UnserviceableComponentListViewModel>> getAllUnserviceableComponentListWithoutPagination() {
        return new ResponseEntity<>(storePartSerialService.getAllUnserviceableComponentListAllData(), HttpStatus.OK);
    }
}
