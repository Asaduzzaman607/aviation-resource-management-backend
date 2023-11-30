package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.VendorSearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.VendorDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.VendorViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ApprovalRequestDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShipmentProviderService implements VendorIService{
    private final VendorService vendorService;

    public ShipmentProviderService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * Custom save method
     *
     * @param {@link ShipmentProviderDto}
     * @return saved message
     */
    @Transactional
    public Vendor create(VendorDto vendorDto) {
        vendorDto.setVendorType(VendorType.SHIPMENT_PROVIDER);
        return vendorService.create(vendorDto);
    }

    /**
     * Custom update method
     *
     * @param id long value
     * @return updated message
     */
    @Transactional
    public Vendor update(VendorDto vendorDto, Long id) {
        vendorDto.setVendorType(VendorType.SHIPMENT_PROVIDER);
        return vendorService.update(vendorDto, id);
    }

    /**
     * Approval or rejected method
     *
     * @param id                 long field
     * @param approvalRequestDto {@link ApprovalRequestDto}
     */
    @Transactional
    public void makeDecision(Long id, ApprovalRequestDto approvalRequestDto, VendorWorkFlowType vendorWorkFlowType) {
        vendorService.makeDecision(id, approvalRequestDto, vendorWorkFlowType);
    }

    /**
     * Change active status
     *
     * @param id       long value
     * @param isActive boolean field
     */
    public void updateActiveStatus(Long id, Boolean isActive, VendorWorkFlowType vendorWorkFlowType) {
        vendorService.updateActiveStatus(id, isActive, vendorWorkFlowType);
    }

    /**
     * Custom search method
     *
     * @param dto      {@link VendorSearchDto}
     * @param pageable page data
     * @return required result
     */
    public PageData search(VendorSearchDto dto, Pageable pageable) {
        dto.setVendorType(VendorType.SHIPMENT_PROVIDER);
        return vendorService.search(dto, pageable);
    }

    /**
     * Single get method
     *
     * @param id long value
     * @return required result
     */
    public VendorViewModel getSingle(Long id) {
        return vendorService.getSingle(id);
    }

}
