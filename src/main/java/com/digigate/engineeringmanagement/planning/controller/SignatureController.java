package com.digigate.engineeringmanagement.planning.controller;

import com.digigate.engineeringmanagement.common.controller.AbstractController;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.IService;
import com.digigate.engineeringmanagement.planning.entity.Signature;
import com.digigate.engineeringmanagement.planning.payload.request.SignatureDto;
import com.digigate.engineeringmanagement.planning.payload.request.SignatureSearchDto;
import com.digigate.engineeringmanagement.planning.payload.response.SignatureViewModel;
import com.digigate.engineeringmanagement.planning.service.SignatureService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Signature Controller
 *
 * @author ashinisingha
 */
@RestController
@RequestMapping("/api/signature")
public class SignatureController extends AbstractController<Signature, SignatureDto> {
    private final SignatureService signatureService;
    private static final String ORDER_BY_AUTH_NO = "authNo";

    /**
     * Parameterized constructor
     *
     * @param service               {@link IService<Signature, SignatureDto > }
     * @param signatureService      {@link SignatureService}
     */
    public SignatureController(IService<Signature, SignatureDto> service, SignatureService signatureService) {
        super(service);
        this.signatureService = signatureService;
    }

    /**
     * This is an API end point of Searching signature
     *
     * @param signatureSearchDto {@link SignatureSearchDto}
     * @param page {@link Integer}
     * @param size {@link Integer}
     * @return pageable search result according to search criteria
     */
    @PostMapping("/search")
    public ResponseEntity<PageData>searchSignature(@RequestBody SignatureSearchDto signatureSearchDto,
                                                   @RequestParam(name = "page", defaultValue = "0") Integer page,
                                                   @RequestParam(name = "size", defaultValue = "10") Integer size
                                                   ){
        if(page > 0) page--;
        Sort sort = Sort.by(Sort.Direction.ASC, ORDER_BY_AUTH_NO);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SignatureViewModel> signatureViewModels = signatureService.searchSignature(signatureSearchDto, pageable);

        PageData pageData = new PageData(
                signatureViewModels.getContent(),
                signatureViewModels.getTotalPages(),
                pageable.getPageNumber() + 1,
                signatureViewModels.getTotalElements()
        );

        return new ResponseEntity<>(pageData, HttpStatus.OK);
    }

    /**
     * This is an API endpoint to getting all active signatures
     *
     * @return          {@link  ResponseEntity<List<SignatureViewModel>>}
     */
    @GetMapping("/all/active")
    public ResponseEntity<List<SignatureViewModel>>getAllActiveList(){
        return ResponseEntity.ok(signatureService.getAllActiveList());
    }

}
