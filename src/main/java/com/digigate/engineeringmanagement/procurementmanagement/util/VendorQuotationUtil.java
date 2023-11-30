package com.digigate.engineeringmanagement.procurementmanagement.util;

import com.digigate.engineeringmanagement.configurationmanagement.dto.projection.VendorProjection;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.VendorService;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.CsQuotationProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.IqItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.PartOrderItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceDetail;
import com.digigate.engineeringmanagement.procurementmanagement.service.PartOrderItemService;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.PartProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RequisitionItemProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StorePartSerialProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreReturnPartDetailsProjection;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.CurrencyService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ProcurementRequisitionItemService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartSerialService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class VendorQuotationUtil {

    private final CurrencyService currencyService;
    private final VendorService vendorService;
    private final StorePartSerialService storePartSerialService;
    private final PartOrderItemService partOrderItemService;
    private final ProcurementRequisitionItemService requisitionItemService;

    public VendorQuotationUtil(CurrencyService currencyService,
                               VendorService vendorService,
                               StorePartSerialService storePartSerialService,
                               PartOrderItemService partOrderItemService,
                               ProcurementRequisitionItemService requisitionItemService) {
        this.currencyService = currencyService;
        this.vendorService = vendorService;
        this.storePartSerialService = storePartSerialService;
        this.partOrderItemService = partOrderItemService;
        this.requisitionItemService = requisitionItemService;
    }

    public Map<Long, Currency> getCurrencyMap(Set<Long> idSet){
        return currencyService.getAllByDomainIdIn(idSet, true)
                .stream()
                .collect(Collectors.toMap(Currency::getId, Function.identity()));
    }

    public Map<Long, VendorProjection> getVendorMap(List<CsQuotationProjection> csQuotationProjectionList){
        Set<Long> vendorIdList = csQuotationProjectionList.stream().map(
                CsQuotationProjection::getQuoteRequestVendorVendorId).collect(Collectors.toSet());
        return vendorService.findVendorByIds(vendorIdList)
                .stream().collect(Collectors.toMap(VendorProjection::getId, Function.identity()));
    }

    public Map<Long, StorePartSerial> getSerialMap(Set<Long> ids){
        return storePartSerialService.findAllByIdIn(ids).stream()
                .collect(Collectors.toMap(StorePartSerial::getId, Function.identity()));
    }

    public Long getItemId(VendorQuotationInvoiceDetail vendorQuotationDetail, RfqType rfqType) {
        if(rfqType == RfqType.LOGISTIC){
            return vendorQuotationDetail.getPoItemId();
        }

        return vendorQuotationDetail.getRequisitionItemId();
    }

    public Map<Long, Long> getIqItemPartIdMap(List<IqItemProjection> iqItemProjections) {
        Map<Long, Long> iqItemAndPartMap = iqItemProjections.stream().collect(Collectors.toMap(
                IqItemProjection::getId, IqItemProjection::getPartId));

        iqItemProjections.stream().filter(item -> Objects.nonNull(item.getAltPartId()))
                .forEach(item -> iqItemAndPartMap.put(item.getId(), item.getAltPartId()));

        return iqItemAndPartMap;
    }

    public Set<Long> getIqItemIds(List<PartOrderItemProjection> partOrderItemProjections) {
        return partOrderItemProjections.stream().map(PartOrderItemProjection::getIqItemId).collect(Collectors.toSet());
    }

    public List<PartOrderItemProjection> getPoItemProjections(List<VendorQuotationInvoiceDetail> vendorQuotationDetailList) {
        Set<Long> poItemIds = vendorQuotationDetailList.stream().map(VendorQuotationInvoiceDetail::getPoItemId).collect(Collectors.toSet());
        return partOrderItemService.findPoItemByIdIn(poItemIds);
    }

    public Set<Long> getPartIds(List<IqItemProjection> iqItemProjections) {
        /** Part id */
        Set<Long> partIds = iqItemProjections.stream().map(IqItemProjection::getPartId).collect(Collectors.toSet());
        /** Alternate part id adding*/
        partIds.addAll(iqItemProjections.stream().map(IqItemProjection::getAltPartId).collect(Collectors.toSet()));
        return partIds;
    }

    public List<RequisitionItemProjection> getReqItemProjections(List<VendorQuotationInvoiceDetail> vendorQuotationDetailList) {
        Set<Long> requisitionItemIds = vendorQuotationDetailList.stream().map(VendorQuotationInvoiceDetail::getRequisitionItemId)
                .collect(Collectors.toSet());
        return requisitionItemService.findRequisitionItemList(requisitionItemIds);
    }

    public Set<Long> getPartIds(List<RequisitionItemProjection> requisitionItemProjections, List<VendorQuotationInvoiceDetail> vendorQuotationDetailList) {
        /** Part id */
        Set<Long> partIds = requisitionItemProjections.stream().map(RequisitionItemProjection::getDemandItemPartId)
                .collect(Collectors.toSet());
        /** Alternate part id adding*/
        partIds.addAll(vendorQuotationDetailList.stream().map(VendorQuotationInvoiceDetail::getAlternatePartId).collect(Collectors.toSet()));
        return partIds;
    }

    public Currency getCurrency(VendorQuotationInvoiceDetail vendorQuotationDetail, Map<Long, Currency> currencyMap) {
        if (nonNull(vendorQuotationDetail.getCurrencyId())) {
            return currencyMap.get(vendorQuotationDetail.getCurrencyId());
        }
        return new Currency();
    }

    public StorePartSerialProjection getSpSerialProjection(VendorQuotationInvoiceDetail vendorQuotationDetail,
                                                           Map<Long, StorePartSerialProjection> spsProjectionMap) {
        if (nonNull(vendorQuotationDetail.getPartSerialId())) {
            return spsProjectionMap.get(vendorQuotationDetail.getPartSerialId());
        }
        return null;
    }

    public String getReasonRemoved(VendorQuotationInvoiceDetail vendorQuotationDetail,
                                   Map<Long, List<StoreReturnPartDetailsProjection>> srpdProjectionMap) {
        if (nonNull(vendorQuotationDetail.getPartSerialId())) {
            Optional<StoreReturnPartDetailsProjection> optionalProjection = srpdProjectionMap
                    .getOrDefault(vendorQuotationDetail.getPartSerialId(), new ArrayList<>()).stream().findFirst();
            if (optionalProjection.isPresent()) {
                return String.valueOf(optionalProjection.get().getReasonRemoved());
            }
        }

        return null;
    }

    public PartProjection getPartProjection(VendorQuotationInvoiceDetail vendorQuotationDetail,
                                            Map<Long, Long> itemAndPartIdMap, Map<Long, PartProjection> partProjectionMap) {
        Long itemId;
        if (nonNull(vendorQuotationDetail.getRequisitionItemId())) {
            itemId = itemAndPartIdMap.get(vendorQuotationDetail.getRequisitionItemId());
        } else {
            itemId = itemAndPartIdMap.get(vendorQuotationDetail.getPoItemId());
        }
        return partProjectionMap.get(itemId);
    }

    public PartProjection getAltPartProjection(VendorQuotationInvoiceDetail vendorQuotationDetail,
                                               Map<Long, PartProjection> partProjectionMap) {
        return partProjectionMap.get(vendorQuotationDetail.getAlternatePartId());
    }
}
