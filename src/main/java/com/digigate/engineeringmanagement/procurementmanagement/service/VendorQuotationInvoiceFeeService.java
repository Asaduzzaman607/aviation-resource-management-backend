package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.procurementmanagement.constant.VendorRequestType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.CurrencyProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VendorQuotationFeeInvoiceDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.VendorQuotationFeeInvoiceViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceFee;
import com.digigate.engineeringmanagement.procurementmanagement.repository.VendorQuotationFeeRepository;
import com.digigate.engineeringmanagement.procurementmanagement.util.VendorQuotationUtil;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.CurrencyService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VendorQuotationInvoiceFeeService extends AbstractSearchService<
        VendorQuotationInvoiceFee, VendorQuotationFeeInvoiceDto, IdQuerySearchDto> {
    private final VendorQuotationUtil vendorQuotationUtil;
    private final CurrencyService currencyService;
    private final VendorQuotationFeeRepository vendorQuotationFeeRepository;

    public VendorQuotationInvoiceFeeService(VendorQuotationFeeRepository vendorQuotationFeeRepository,
                                            VendorQuotationUtil vendorQuotationUtil, CurrencyService currencyService) {
        super(vendorQuotationFeeRepository);
        this.vendorQuotationUtil = vendorQuotationUtil;
        this.vendorQuotationFeeRepository = vendorQuotationFeeRepository;
        this.currencyService = currencyService;
    }

    public List<VendorQuotationInvoiceFee> findByVendorQuotationInvoiceId(Long oldQuotationId) {
        return vendorQuotationFeeRepository.findByVendorQuotationInvoiceIdAndVendorRequestType(oldQuotationId, VendorRequestType.QUOTATION);
    }

    public List<VendorQuotationFeeInvoiceViewModel> getAllVendorQuotationFeeByType(Long id, VendorRequestType quotation) {
        return getAllVendorQuotationFee(vendorQuotationFeeRepository.findByVendorQuotationInvoiceIdAndVendorRequestType(id,quotation));
    }

    public List<VendorQuotationFeeInvoiceViewModel> getAllVendorQuotationFee(List<VendorQuotationInvoiceFee> vendorQuotationFeeList) {
        Set<Long> collectionsOfCurrencyIds = vendorQuotationFeeList.stream()
                .map(VendorQuotationInvoiceFee::getCurrencyId).collect(Collectors.toSet());
        Map<Long, CurrencyProjection> currencyProjectionMap = currencyService.findCurrencyByIdIn(collectionsOfCurrencyIds)
                .stream().collect(Collectors.toMap(CurrencyProjection::getId, Function.identity()));

        return vendorQuotationFeeList.stream().map(vendorQuotationFee -> convertAllToResponseDto(vendorQuotationFee,
                currencyProjectionMap.get(vendorQuotationFee.getCurrencyId()))).collect(Collectors.toList());
    }

    public void createOrUpdateFees(List<VendorQuotationFeeInvoiceDto> vendorQuotationFeeInvoiceDtoList,
                                   Long vendorQuotationInvoiceId,
                                   VendorRequestType vendorRequestType,
                                   Long initialId) {

        Set<Long> updateIdList = vendorQuotationFeeInvoiceDtoList.stream().map(VendorQuotationFeeInvoiceDto::getId).collect(Collectors.toSet());

        Map<Long, VendorQuotationInvoiceFee> vendorQuotationFeeMap = getAllByDomainIdIn(updateIdList, true).stream()
                .filter(vendorQuotationFee -> {
                    if(!Objects.equals(vendorQuotationFee.getVendorQuotationInvoiceId(), initialId)){
                        throw EngineeringManagementServerException.badRequest(ErrorId.VALID_VENDOR_FEE_ID_REQUIRED);
                    }
                    return true;
                }).collect(Collectors.toMap(VendorQuotationInvoiceFee::getId, Function.identity()));

        Set<Long> currencyIdList = vendorQuotationFeeInvoiceDtoList.stream().map(VendorQuotationFeeInvoiceDto::getCurrencyId)
                .collect(Collectors.toSet());
        Map<Long, Currency> currencyMap = vendorQuotationUtil.getCurrencyMap(currencyIdList);

        List<VendorQuotationInvoiceFee> vendorQuotationFeeList = vendorQuotationFeeInvoiceDtoList.stream().map(
                vendorQuotationFeeInvoiceDto -> convertQuotationToEntity(vendorQuotationFeeInvoiceDto, vendorQuotationFeeMap.getOrDefault(
                        vendorQuotationFeeInvoiceDto.getId(), new VendorQuotationInvoiceFee()), vendorQuotationInvoiceId,
                        currencyMap.get(vendorQuotationFeeInvoiceDto.getCurrencyId()), vendorRequestType)).collect(Collectors.toList());

        saveItemList(vendorQuotationFeeList);
    }

    @Override
    protected Specification<VendorQuotationInvoiceFee> buildSpecification(IdQuerySearchDto searchDto) {
        return null;
    }

    @Override
    protected VendorQuotationFeeInvoiceViewModel convertToResponseDto(VendorQuotationInvoiceFee vendorQuotationFee) {
        return getAllVendorQuotationFee(Collections.singletonList(vendorQuotationFee))
                .get(ApplicationConstant.FIRST_INDEX);
    }

    @Override
    protected VendorQuotationInvoiceFee convertToEntity(VendorQuotationFeeInvoiceDto vendorQuotationFeeInvoiceDto) {
        return null;
    }

    @Override
    protected VendorQuotationInvoiceFee updateEntity(VendorQuotationFeeInvoiceDto dto, VendorQuotationInvoiceFee entity) {
        return null;
    }

    private VendorQuotationInvoiceFee convertQuotationToEntity(VendorQuotationFeeInvoiceDto vendorQuotationFeeInvoiceDto,
                                                               VendorQuotationInvoiceFee vendorQuotationInvoiceFee,
                                                               Long vendorQuotationInvoiceId,
                                                               Currency currency,
                                                               VendorRequestType vendorRequestType) {

        vendorQuotationInvoiceFee.setVendorQuotationInvoiceId(vendorQuotationInvoiceId);
        vendorQuotationInvoiceFee.setFeeName(vendorQuotationFeeInvoiceDto.getFeeName());
        vendorQuotationInvoiceFee.setFeeCost(vendorQuotationFeeInvoiceDto.getFeeCost());
        vendorQuotationInvoiceFee.setVendorRequestType(vendorRequestType);
        vendorQuotationInvoiceFee.setCurrency(currency);
        if(BooleanUtils.isFalse(vendorQuotationFeeInvoiceDto.getIsActive())){
            vendorQuotationInvoiceFee.setIsActive(vendorQuotationFeeInvoiceDto.getIsActive());
        }

        return vendorQuotationInvoiceFee;
    }

    private VendorQuotationFeeInvoiceViewModel convertAllToResponseDto(VendorQuotationInvoiceFee vendorQuotationInvoiceFee,
                                                                       CurrencyProjection currencyProjection) {
        VendorQuotationFeeInvoiceViewModel vendorQuotationFeeInvoiceViewModel = new VendorQuotationFeeInvoiceViewModel();
        vendorQuotationFeeInvoiceViewModel.setId(vendorQuotationInvoiceFee.getId());
        vendorQuotationFeeInvoiceViewModel.setFeeName(vendorQuotationInvoiceFee.getFeeName());
        vendorQuotationFeeInvoiceViewModel.setFeeCost(vendorQuotationInvoiceFee.getFeeCost());
        if (Objects.nonNull(currencyProjection)) {
            vendorQuotationFeeInvoiceViewModel.setCurrencyId(currencyProjection.getId());
            vendorQuotationFeeInvoiceViewModel.setCurrencyCode(currencyProjection.getCode());
        }
        vendorQuotationFeeInvoiceViewModel.setVendorRequestType(vendorQuotationInvoiceFee.getVendorRequestType());
        vendorQuotationFeeInvoiceViewModel.setIsActive(vendorQuotationInvoiceFee.getIsActive());
        return vendorQuotationFeeInvoiceViewModel;
    }
}
