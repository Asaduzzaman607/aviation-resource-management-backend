package com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.CurrencyProjection;
import com.digigate.engineeringmanagement.procurementmanagement.service.VendorQuotationInvoiceDetailService;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration.CurrencyRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.storeconfiguration.CurrencyResponseDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration.CurrencyRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartSerialService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class CurrencyService extends AbstractSearchService<Currency, CurrencyRequestDto, IdQuerySearchDto> {

    private final CurrencyRepository currencyRepository;
    private final VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService;
    private final StorePartSerialService storePartSerialService;

    @Autowired
    public CurrencyService(AbstractRepository<Currency> repository, CurrencyRepository currencyRepository,
                           @Lazy VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService,
                           @Lazy StorePartSerialService storePartSerialService) {
        super(repository);
        this.currencyRepository = currencyRepository;
        this.vendorQuotationInvoiceDetailService = vendorQuotationInvoiceDetailService;
        this.storePartSerialService = storePartSerialService;
    }

    @Override
    public Currency create(CurrencyRequestDto currencyRequestDto) {
        validate(currencyRequestDto, null);
        return super.create(currencyRequestDto);
    }

    @Override
    public Currency update(CurrencyRequestDto dto, Long id) {
        Currency currency = findByIdUnfiltered(id);
        validate(dto, currency);
        final Currency entity = updateEntity(dto, currency);
        return super.saveItem(entity);
    }

    @Override
    public void updateActiveStatus(Long id, Boolean isActive) {
        Currency currency = findByIdUnfiltered(id);
        if (currency.getIsActive() == isActive) {
            throw EngineeringManagementServerException.badRequest(ErrorId.ONLY_TOGGLE_VALUE_ACCEPTED);
        }
        if (isActive == Boolean.FALSE && (vendorQuotationInvoiceDetailService.existsByCurrencyIdAndIsActiveTrue(id)
                || storePartSerialService.existsByCurrencyIdAndIsActiveTrue(id))) {
            throw EngineeringManagementServerException.badRequest(ErrorId.CHILD_DATA_EXISTS);
        }
        super.updateActiveStatus(id, isActive);
    }

    public Collection<CurrencyProjection> findCurrencyByIdIn(Set<Long> collectionsOfCurrencyIds) {
        return currencyRepository.findCurrencyByIdIn(collectionsOfCurrencyIds);
    }

    @Override
    protected Specification<Currency> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<Currency> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.ENTITY_CODE));
    }

    @Override
    protected CurrencyResponseDto convertToResponseDto(Currency currency) {
        return CurrencyResponseDto.builder()
                .id(currency.getId())
                .code(currency.getCode())
                .description(currency.getDescription())
                .build();
    }

    @Override
    protected Currency convertToEntity(CurrencyRequestDto currencyRequestDto) {
        Currency currency = new Currency();
        currency.setCode(currencyRequestDto.getCode());
        currency.setDescription(currencyRequestDto.getDescription());
        return currency;
    }

    @Override
    protected Currency updateEntity(CurrencyRequestDto dto, Currency entity) {
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    private void validate(CurrencyRequestDto dto, Currency old) {
        List<Currency> currencies = currencyRepository.findByCode(dto.getCode());
        if (!CollectionUtils.isEmpty(currencies)) {
            currencies.forEach(currency -> {
                if (Objects.nonNull(old) && currency.equals(old)) {
                    return;
                }
                throw EngineeringManagementServerException.badRequest(
                        ErrorId.CURRENCY_CODE_EXISTS);
            });
        }
    }
}
