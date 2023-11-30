package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.planning.payload.request.AmlRecordRequest;
import com.digigate.engineeringmanagement.planning.payload.request.OilRecordSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.AmlOilRecordDto;
import com.digigate.engineeringmanagement.planning.service.AmlOilRecordIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * OIl Record Controller
 *
 * @author Sayem Hasnat
 */
@RestController
@RequestMapping("/api/oil-record")
public class AmlOilRecordController {

    private final AmlOilRecordIService oilRecordService;


    /**
     * Autowired Constructor
     *
     * @param oilRecordService {@link AmlOilRecordIService}
     */
    @Autowired
    public AmlOilRecordController(AmlOilRecordIService oilRecordService) {
        this.oilRecordService = oilRecordService;
    }

    /**
     * This controller method responsible for find Aml Oil Record by AML ID
     *
     * @param oilRecordSearchDto {@link OilRecordSearchDto}
     */
    @PostMapping("/search")
    public List<AmlOilRecordDto> getOilRecordByAmlId(@RequestBody OilRecordSearchDto oilRecordSearchDto) {
        return oilRecordService.getOilRecordByAmlId(oilRecordSearchDto);
    }

    /**
     * This controller method responsible for find Aml Oil Record by AML ID
     *
     * @param amlId            {@link Long}
     * @param amlRecordRequest {@link AmlRecordRequest}
     */
    @PostMapping("/{amlId}")
    public ResponseEntity<MessageResponse> saveRecord(@PathVariable Long amlId,
                                                      @Valid @RequestBody AmlRecordRequest amlRecordRequest) {
        oilRecordService.saveAllRecords(amlRecordRequest, amlId);
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.CREATED_SUCCESSFULLY_MESSAGE, amlId));
    }

    /**
     * This controller method responsible for find Aml Oil Record by AML ID
     *
     * @param amlRecordRequest {@link AmlRecordRequest}
     */
    @Transactional
    @PutMapping("/{amlId}")
    public ResponseEntity<MessageResponse> update(@PathVariable Long amlId,
                                                  @Valid @RequestBody AmlRecordRequest amlRecordRequest) {
        oilRecordService.updateAllRecords(amlRecordRequest, amlId);
        return ResponseEntity.ok(new MessageResponse(ApplicationConstant.UPDATED_SUCCESSFULLY_MESSAGE,
                amlRecordRequest.getOnArrival().getAmlId()));
    }

}

