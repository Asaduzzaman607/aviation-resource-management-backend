package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.common.authentication.security.services.UserDetailsImpl;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.VendorSearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.QualitySaveValidityDateReqDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.VendorDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.VendorViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import com.digigate.engineeringmanagement.configurationmanagement.repository.configuration.VendorRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class SupplierService implements VendorIService {
    private final VendorService vendorService;

    /**
     * Autowired
     */
    public SupplierService(@Lazy VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @Transactional
    public Vendor create(VendorDto vendorDto) {
        vendorDto.setVendorType(VendorType.SUPPLIER);
        return vendorService.create(vendorDto);
    }

    @Transactional
    public Vendor update(VendorDto vendorDto, Long id) {
        vendorDto.setVendorType(VendorType.SUPPLIER);
        return vendorService.update(vendorDto, id);
    }

    public PageData search(VendorSearchDto dto, Pageable pageable) {
        dto.setVendorType(VendorType.SUPPLIER);
        return vendorService.search(dto, pageable);
    }

    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto, VendorWorkFlowType vendorWorkFlowType) {
        vendorService.makeDecision(id, approvalRequestDto, vendorWorkFlowType);
    }

    public VendorViewModel getSingle(Long id) {
        return vendorService.getSingle(id);
    }

    public void updateActiveStatus(Long id, Boolean isActive, VendorWorkFlowType vendorWorkFlowType) {
        vendorService.updateActiveStatus(id, isActive, vendorWorkFlowType);
    }

    public Vendor saveValidityDate(Long id, QualitySaveValidityDateReqDto qualitySaveValidityDateReqDto) {
        return vendorService.saveValidityDate(id, qualitySaveValidityDateReqDto);
    }
}
