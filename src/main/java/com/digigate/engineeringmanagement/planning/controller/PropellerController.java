package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.ISearchService;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.common.util.ReportUtil;
import com.digigate.engineeringmanagement.planning.entity.Propeller;
import com.digigate.engineeringmanagement.planning.payload.request.MappingDto;
import com.digigate.engineeringmanagement.planning.payload.request.PropellerDto;
import com.digigate.engineeringmanagement.planning.payload.request.PropellerSearchDto;
import com.digigate.engineeringmanagement.planning.service.PropellerService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Propeller Controller
 *
 * @author Masud Rana
 */
@RestController
@RequestMapping("/api/propeller")
public class PropellerController extends AbstractSearchController<Propeller, PropellerDto, PropellerSearchDto> {
    private final PropellerService propellerService;
    private static final String SUCCESSFULLY_APPLIED_MESSAGE = "Successfully applied";
    private static final String FILE_TYPE = "pdf";
    private static final String PROPELLER_REPORT = "propellerReport";

    /**
     * Parameterized constructor
     *
     * @param service          {@link IService}
     * @param propellerService {@link PropellerService}
     */
    public PropellerController(ISearchService<Propeller, PropellerDto, PropellerSearchDto> service,
                               PropellerService propellerService) {
        super(service);
        this.propellerService = propellerService;
    }

    /**
     * make relation between entity
     *
     * @param mappingDto {@link MappingDto}
     * @return {@link MessageResponse}
     */
    @PutMapping("/apply")
    public ResponseEntity<MessageResponse> apply(@RequestBody MappingDto mappingDto) {
        propellerService.apply(mappingDto);
        return new ResponseEntity<>(new MessageResponse(SUCCESSFULLY_APPLIED_MESSAGE), HttpStatus.OK);
    }

    /**
     * Report for entity
     *
     * @param searchDto {@link PropellerSearchDto}
     * @return {@link PageData}
     */
    @PostMapping("/report")
    public ResponseEntity<byte[]> search(@RequestBody PropellerSearchDto searchDto) {
        byte[] reportByteData = propellerService.getReport(searchDto, FILE_TYPE);
        String contentType = ReportUtil.prepareContentType(FILE_TYPE);
        HttpHeaders httpHeaders = ReportUtil.prepareHttpHeader(FILE_TYPE, PROPELLER_REPORT);
        return ResponseEntity
                .ok()
                .header(ApplicationConstant.CONTENT_TYPE, contentType + ApplicationConstant.SEMICOLON
                        + ApplicationConstant.CHARSET_UTF_8)
                .headers(httpHeaders)
                .body(reportByteData);
    }

}
