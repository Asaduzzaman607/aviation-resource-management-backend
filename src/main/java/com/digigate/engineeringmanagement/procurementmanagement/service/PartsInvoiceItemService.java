package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.RequisitionItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.PartsInvoiceItemReqDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.PartInvoiceItemResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrderItem;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartsInvoice;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartsInvoiceItem;
import com.digigate.engineeringmanagement.procurementmanagement.repository.PartInvoiceItemRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.CurrencyService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PartsInvoiceItemService extends AbstractSearchService<PartsInvoiceItem, PartsInvoiceItemReqDto, IdQuerySearchDto> {
    private final PartInvoiceItemRepository partInvoiceItemRepository;
    private final CurrencyService currencyService;

    public PartsInvoiceItemService(AbstractRepository<PartsInvoiceItem> repository, PartInvoiceItemRepository partInvoiceItemRepository, CurrencyService currencyService) {
        super(repository);
        this.partInvoiceItemRepository = partInvoiceItemRepository;
        this.currencyService = currencyService;
    }


    public List<PartsInvoiceItem> saveAll(List<PartOrderItem> partOrderItemList, PartsInvoice partsInvoice) {

        return partInvoiceItemRepository.saveAll(partOrderItemList.stream().map(partOrderItem ->
                convertSaveEntity(partOrderItem, partsInvoice)).collect(Collectors.toList()));
    }



    private PartsInvoiceItem convertSaveEntity(PartOrderItem partOrderItem, PartsInvoice partsInvoice) {
        PartsInvoiceItem partsInvoiceItem = new PartsInvoiceItem();
        partsInvoiceItem.setPartOrderItem(partOrderItem);
        partsInvoiceItem.setPartsInvoice(partsInvoice);
        return partsInvoiceItem;
    }

    @Override
    protected Specification<PartsInvoiceItem> buildSpecification(IdQuerySearchDto searchDto) {
        return null;
    }

    @Override
    protected <T> T convertToResponseDto(PartsInvoiceItem partsInvoiceItem) {
        return null;
    }

    @Override
    protected PartsInvoiceItem convertToEntity(PartsInvoiceItemReqDto partsInvoiceItemReqDto) {
        return null;
    }

    @Override
    protected PartsInvoiceItem updateEntity(PartsInvoiceItemReqDto dto, PartsInvoiceItem entity) {
        return null;
    }

    public List<PartInvoiceItemResponseDto> getAllResponse(Set<Long> invoiceIds, RfqType rfqType) {
        List<RequisitionItemProjection> requisitionItemProjectionList = rfqType.equals(RfqType.PROCUREMENT)?
                partInvoiceItemRepository.getProcurementRequisitionInfoByPiIds(invoiceIds):
                partInvoiceItemRepository.getProcurementRequisitionInfoByPiIdsForLogistic(invoiceIds);

        return requisitionItemProjectionList.stream().map(this::convertResponse).collect(Collectors.toList());
    }

    private PartInvoiceItemResponseDto convertResponse(RequisitionItemProjection requisitionItemProjection) {
        PartInvoiceItemResponseDto partInvoiceItemResponseDto = PartInvoiceItemResponseDto.builder()
                .id(requisitionItemProjection.getId())
                .poItemId(requisitionItemProjection.getPoItemId())
                .partInvoiceId(requisitionItemProjection.getPartInvoiceId())
                .paymentMode(requisitionItemProjection.getPaymentMode())
                .remarks(requisitionItemProjection.getRemarks())
                .isPartiallyApproved(requisitionItemProjection.getIsPartiallyApproved())
                .paymentCurrencyId(requisitionItemProjection.getPaymentCurrencyId())
                .paymentCurrencyCode(requisitionItemProjection.getPaymentCurrencyCode())
                .alreadyApprovedQuantity(requisitionItemProjection.getApprovedQuantity())
                .quantity(requisitionItemProjection.getQuantity())
                .uomId(requisitionItemProjection.getUomId())
                .uomCode(requisitionItemProjection.getUomCode())
                .priorityType(requisitionItemProjection.getPriorityType())
                .unitPrice(requisitionItemProjection.getUnitPrice())
                .condition(requisitionItemProjection.getCondition())
                .leadTime(requisitionItemProjection.getLeadTime())
                .currencyId(requisitionItemProjection.getCurrencyId())
                .currencyCode(requisitionItemProjection.getCurrencyCode())
                .airCraftId(requisitionItemProjection.getAirCraftId())
                .airCraftName(requisitionItemProjection.getAirCraftName())
                .vendorId(requisitionItemProjection.getVendorId())
                .vendorName(requisitionItemProjection.getVendorName())
                .build();
        if (Objects.nonNull(requisitionItemProjection.getAlterPartId())) {
            partInvoiceItemResponseDto.setPartId(requisitionItemProjection.getAlterPartId());
            partInvoiceItemResponseDto.setPartNo(requisitionItemProjection.getAlterPartNo());
            partInvoiceItemResponseDto.setPartDescription(requisitionItemProjection.getAlterPartDescription());
        } else {
            partInvoiceItemResponseDto.setPartId(requisitionItemProjection.getPartId());
            partInvoiceItemResponseDto.setPartNo(requisitionItemProjection.getPartNo());
            partInvoiceItemResponseDto.setPartDescription(requisitionItemProjection.getPartDescription());
        }
        return partInvoiceItemResponseDto;
    }

    public void updatePartiallyApproved(List<PartsInvoiceItemReqDto> partsInvoiceItemReqDtoList) {
        Set<Long> invoiceItemIds = partsInvoiceItemReqDtoList.stream().map(PartsInvoiceItemReqDto::getId).collect(Collectors.toSet());
        Map<Long, PartsInvoiceItem> partsInvoiceItemMap = getAllByDomainIdIn(invoiceItemIds, true).stream().collect(Collectors.toMap(PartsInvoiceItem::getId, Function.identity()));
        saveItemList(partsInvoiceItemReqDtoList.stream().map(item -> convertPartiallyApprovedEntity(item, partsInvoiceItemMap.get(item.getId()))).collect(Collectors.toList()));
    }

    private PartsInvoiceItem convertPartiallyApprovedEntity(PartsInvoiceItemReqDto partsInvoiceItemReqDto, PartsInvoiceItem partsInvoiceItem) {
        if (Objects.nonNull(partsInvoiceItemReqDto.getApprovedQuantity())) {

            if (Objects.isNull(partsInvoiceItemReqDto.getPaymentCurrencyId()) || Objects.isNull(partsInvoiceItemReqDto.getPaymentMode())) {
                throw EngineeringManagementServerException.badRequest(ErrorId.PAYMENT_CURRENCY_PAYMENT_MODE_MANDATORY);
            }
            Integer ApprovedQty = partsInvoiceItemReqDto.getApprovedQuantity() + partsInvoiceItem.getApprovedQuantity();
            if (partsInvoiceItemReqDto.getQuantity() < ApprovedQty) {
                throw EngineeringManagementServerException.badRequest(ErrorId.APPROVED_QUANTITY_IS_NOT_GREATER_THEN_REQUESTED_QUANTITY);
            }
            partsInvoiceItem.setApprovedQuantity(ApprovedQty);
            partsInvoiceItem.setPaymentMode(partsInvoiceItemReqDto.getPaymentMode());
            partsInvoiceItem.setPaymentCurrency(currencyService.findByIdUnfiltered(partsInvoiceItemReqDto.getPaymentCurrencyId()));
            partsInvoiceItem.setRemarks(partsInvoiceItemReqDto.getRemarks());
            if (Objects.equals(partsInvoiceItemReqDto.getQuantity(), ApprovedQty)) {
                partsInvoiceItem.setIsPartiallyApproved(true);
            }
        }
        return partsInvoiceItem;
    }

    public List<PartsInvoiceItem> findByIdIn(Set<Long> collectionOfPartInvoiceItemIds) {
        return partInvoiceItemRepository.findAllByIdInAndIsActive(collectionOfPartInvoiceItemIds,true);
    }
}
