package com.digigate.engineeringmanagement.configurationmanagement.controller.administration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.MessageResponse;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration.ConfigModuleDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.ConfigModuleResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.service.administration.IModuleService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.ConfigMenuSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/module")
public class ConfigModuleController {
    private static final String UPDATED_SUCCESSFULLY_MESSAGE = "Updated Successfully";
    private static final String ACTIVE_STATUS_CHANGED_SUCCESSFULLY_MESSAGE = "Active Status Changed Successfully";
    private final IModuleService service;

    public ConfigModuleController(IModuleService service) {
        this.service = service;
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<MessageResponse> updateActiveStatus(@PathVariable Long id,
                                                              @RequestParam("active") Boolean isActive) {
        service.updateActiveStatus(id, isActive);
        return ResponseEntity.ok(new MessageResponse(ACTIVE_STATUS_CHANGED_SUCCESSFULLY_MESSAGE));
    }

    /**
     * search entities by criteria
     *
     * @param searchDto {@link IdQuerySearchDto}
     * @param pageable  {@link Pageable}
     * @return {@link PageData}
     */
    @PostMapping("/search")
    public ResponseEntity<PageData> search(@RequestBody @Valid ConfigMenuSearchDto searchDto,
                                           @PageableDefault(
                                               sort = ApplicationConstant.DEFAULT_SORT,
                                               direction = Sort.Direction.ASC) Pageable pageable) {
        return new ResponseEntity<>(service.search(searchDto, pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ConfigModuleResponseDto getSingle(@PathVariable Long id) {
        return service.getSingle(id);
    }

}
