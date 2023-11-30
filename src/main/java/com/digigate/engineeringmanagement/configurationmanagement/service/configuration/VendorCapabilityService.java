package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.VendorCapabilityRequestDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.VendorCapabilityResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.VendorCapability;
import com.digigate.engineeringmanagement.configurationmanagement.repository.configuration.VendorCapabilityRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class VendorCapabilityService extends AbstractSearchService<VendorCapability, VendorCapabilityRequestDto,
        IdQuerySearchDto> {

    private final VendorCapabilityRepository vendorCapabilityRepository;

    public VendorCapabilityService(VendorCapabilityRepository vendorCapabilityRepository) {
        super(vendorCapabilityRepository);
        this.vendorCapabilityRepository = vendorCapabilityRepository;
    }

    @Override
    protected Specification<VendorCapability> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<VendorCapability> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.NAME));
    }

    @Override
    protected VendorCapabilityResponseDto convertToResponseDto(VendorCapability vendorCapability) {
        VendorCapabilityResponseDto vendorResponseDto = new VendorCapabilityResponseDto();
        vendorResponseDto.setId(vendorCapability.getId());
        vendorResponseDto.setName(vendorCapability.getName());
        return vendorResponseDto;
    }

    @Override
    protected VendorCapability convertToEntity(VendorCapabilityRequestDto vendorRequestDto) {
        VendorCapability vendorCapability = new VendorCapability();
        vendorCapability.setName(vendorRequestDto.getName());
        return vendorCapability;
    }

    @Override
    protected VendorCapability updateEntity(VendorCapabilityRequestDto dto, VendorCapability entity) {
        entity.setName(dto.getName());
        return entity;
    }

    public List<VendorCapability> findByIdInAndIsActiveTrue(Set<Long> ids) {
        return vendorCapabilityRepository.findByIdInAndIsActiveTrue(ids);
    }
}
