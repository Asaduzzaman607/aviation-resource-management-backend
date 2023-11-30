package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.CapabilitySearchDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.VendorCapabilityLogRequestDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.VendorCapabilityLogViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.VendorCapabilityViewModel;
import com.digigate.engineeringmanagement.configurationmanagement.entity.VendorCapabilityLog;
import com.digigate.engineeringmanagement.configurationmanagement.repository.configuration.VendorCapabilityLogRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.IS_ACTIVE_FIELD;

@Service
public class VendorCapabilityLogService extends AbstractSearchService<VendorCapabilityLog, VendorCapabilityLogRequestDto,
        CapabilitySearchDto> {

    private final VendorCapabilityLogRepository vendorCapabilityLogRepository;
    private final VendorCapabilityService vendorCapabilityService;
    private final VendorService vendorService;

    public VendorCapabilityLogService(VendorCapabilityLogRepository vendorCapabilityLogRepository,
                                      @Lazy VendorCapabilityService vendorCapabilityService,
                                      @Lazy VendorService vendorService) {
        super(vendorCapabilityLogRepository);
        this.vendorCapabilityLogRepository = vendorCapabilityLogRepository;
        this.vendorCapabilityService = vendorCapabilityService;
        this.vendorService = vendorService;
    }

    public void saveAll(List<VendorCapabilityLogRequestDto> dtos, Long vendorId) {
        List<VendorCapabilityLog> vendorCapabilityLogList = populateDtoListToEntityList(dtos, vendorId);
        super.saveItemList(vendorCapabilityLogList);
    }

    @Override
    protected Specification<VendorCapabilityLog> buildSpecification(CapabilitySearchDto searchDto) {
        CustomSpecification<VendorCapabilityLog> customSpecification = new CustomSpecification<>();

        return Specification.where(customSpecification.active(searchDto.getIsActive(), IS_ACTIVE_FIELD)
                .and(customSpecification.equalSpecificationAtChild(searchDto.getType(), "vendor", ApplicationConstant.VENDOR_TYPE))
                .and(customSpecification.likeSpecificationAtChild(searchDto.getQuery(), "vendor", ApplicationConstant.NAME)));
  }

    @Override
    protected VendorCapabilityLogViewModel convertToResponseDto(VendorCapabilityLog vendorCapabilityLog) {
        VendorCapabilityLogViewModel vendorCapabilityLogViewModel = new VendorCapabilityLogViewModel();
        vendorCapabilityLogViewModel.setVendorType(vendorCapabilityLog.getVendor().getVendorType());
        vendorCapabilityLogViewModel.setParentId(vendorCapabilityLog.getVendorId());
        vendorCapabilityLogViewModel.setParentName(vendorCapabilityLog.getVendor().getName());
        vendorCapabilityLogViewModel.setLogStatus(Collections.singletonList(VendorCapabilityViewModel
                .of(vendorCapabilityLog.getVendorId(), vendorCapabilityLog.getVendorCapabilityId(), true)));
        return vendorCapabilityLogViewModel;
    }

    @Override
    protected VendorCapabilityLog convertToEntity(VendorCapabilityLogRequestDto vendorCapabilityLogRequestDto) {
        return null;
    }

    @Override
    protected VendorCapabilityLog updateEntity(VendorCapabilityLogRequestDto dto, VendorCapabilityLog entity) {
        return null;
    }

    private List<VendorCapabilityLog> populateDtoListToEntityList(List<VendorCapabilityLogRequestDto> vendorCapabilityLogRequestDtoList,
                                                                  Long vendorId) {

        Set<Long> updateIdList = vendorCapabilityLogRequestDtoList
                .stream()
                .map(VendorCapabilityLogRequestDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, VendorCapabilityLog> vendorCapabilityLogMap = vendorCapabilityLogRepository.findAllByIdIn(updateIdList)
                .stream()
                .collect(Collectors.toMap(VendorCapabilityLog::getId, Function.identity()));

        return vendorCapabilityLogRequestDtoList
                .stream()
                .map(vendorCapabilityLogRequestDto -> populateToEntity(
                        vendorCapabilityLogRequestDto,
                        vendorCapabilityLogMap.getOrDefault(vendorCapabilityLogRequestDto.getId(), new VendorCapabilityLog()),
                        vendorId))
                .collect(Collectors.toList());
    }

    private VendorCapabilityLog populateToEntity(VendorCapabilityLogRequestDto vendorCapabilityLogRequestDto,
                                                 VendorCapabilityLog vendorCapabilityLog, Long vendorId) {
        vendorCapabilityLog.setVendorCapability(vendorCapabilityService
                .findById(vendorCapabilityLogRequestDto.getVendorCapabilityId()));
        vendorCapabilityLog.setStatus(vendorCapabilityLogRequestDto.isStatus());
        if (Objects.nonNull(vendorId)) {
            vendorCapabilityLog.setVendor(vendorService.findById(vendorId));
        }
        return vendorCapabilityLog;
    }

    public List<VendorCapabilityLog> findAllByVendorId(Long id) {
        return vendorCapabilityLogRepository.findAllByVendorId(id);
    }

    public List<VendorCapabilityLog> findAllByVendorIdIn(Set<Long> ids) {
        return vendorCapabilityLogRepository.findAllByVendorIdIn(ids);
    }
}
