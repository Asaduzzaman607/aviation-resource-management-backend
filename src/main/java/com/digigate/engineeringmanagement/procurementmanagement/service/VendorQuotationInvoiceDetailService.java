package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.planning.service.PartService;
import com.digigate.engineeringmanagement.planning.service.PartWiseUomService;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.OrderType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.VendorRequestType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.IqItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.PartOrderItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.QuoteRequestProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.VqDetailProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VendorQuotationInvoiceDetailDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VqdQuantity;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VqdQuantityDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.VendorQuotationInvoiceDetailViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrder;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrderItem;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceDetail;
import com.digigate.engineeringmanagement.procurementmanagement.repository.VendorQuotationDetailRepository;
import com.digigate.engineeringmanagement.procurementmanagement.util.PoUtilService;
import com.digigate.engineeringmanagement.procurementmanagement.util.VendorQuotationUtil;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
import com.digigate.engineeringmanagement.status.serviceImpl.DemandStatusServiceImpl;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisitionItem;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.*;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.UnitMeasurementService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ProcurementRequisitionItemService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ReturnPartsDetailService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreDemandDetailsService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StorePartSerialService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
public class VendorQuotationInvoiceDetailService extends AbstractSearchService<
        VendorQuotationInvoiceDetail,
        VendorQuotationInvoiceDetailDto,
        IdQuerySearchDto> {
    private final QuoteRequestService quoteRequestService;
    private final PartOrderItemService partOrderItemService;
    private final ProcurementRequisitionItemService requisitionItemService;
    private final VendorQuotationUtil vendorQuotationUtil;
    private final PartService partService;
    private final UnitMeasurementService unitMeasurementService;
    private final ReturnPartsDetailService returnPartsDetailService;
    private final VendorQuotationDetailRepository vendorQuotationDetailRepository;
    private final StorePartSerialService storePartSerialService;
    private final PoUtilService poUtilService;
    private final PartWiseUomService partWiseUomService;
    private final DemandStatusServiceImpl demandStatusServiceImpl;
    private final PartOrderService partOrderService;
    private final DemandStatusService demandStatusService;
    private final StoreDemandDetailsService storeDemandDetailsService;

    public VendorQuotationInvoiceDetailService(QuoteRequestService quoteRequestService,
                                               PartOrderItemService partOrderItemService,
                                               VendorQuotationDetailRepository vendorQuotationDetailRepository,
                                               ProcurementRequisitionItemService requisitionItemService,
                                               VendorQuotationUtil vendorQuotationUtil,
                                               PartService partService,
                                               UnitMeasurementService unitMeasurementService,
                                               ReturnPartsDetailService returnPartsDetailService,
                                               StorePartSerialService storePartSerialService,
                                               @Lazy PoUtilService poUtilService,
                                               PartWiseUomService partWiseUomService,
                                               DemandStatusServiceImpl demandStatusServiceImpl,
                                               @Lazy PartOrderService partOrderService,
                                               DemandStatusService demandStatusService,
                                               StoreDemandDetailsService storeDemandDetailsService) {
        super(vendorQuotationDetailRepository);
        this.quoteRequestService = quoteRequestService;
        this.partOrderItemService = partOrderItemService;
        this.requisitionItemService = requisitionItemService;
        this.vendorQuotationUtil = vendorQuotationUtil;
        this.partService = partService;
        this.vendorQuotationDetailRepository = vendorQuotationDetailRepository;
        this.unitMeasurementService = unitMeasurementService;
        this.returnPartsDetailService = returnPartsDetailService;
        this.storePartSerialService = storePartSerialService;
        this.poUtilService = poUtilService;
        this.partWiseUomService = partWiseUomService;
        this.demandStatusServiceImpl = demandStatusServiceImpl;
        this.partOrderService = partOrderService;
        this.demandStatusService = demandStatusService;
        this.storeDemandDetailsService = storeDemandDetailsService;
    }

    public List<IqItemProjection> findDetailsByVendorQuotationIdIn(Set<Long> quotationIdList, VendorRequestType type) {
        return vendorQuotationDetailRepository.findByVendorQuotationInvoiceIdInAndVendorRequestType(quotationIdList, type);
    }

    public List<IqItemProjection> findDetailsByVendorQuotationIdInForLogistic(Set<Long> quotationIdList, VendorRequestType type) {
        return vendorQuotationDetailRepository.findByVendorQuotationInvoiceIdInAndVendorRequestTypeForLogistic(quotationIdList, type);
    }

    public List<IqItemProjection> findDetailsByIdIn(Set<Long> detailIds, VendorRequestType type) {
        return vendorQuotationDetailRepository.findByIdInAndVendorRequestType(detailIds, type);
    }

    public List<VendorQuotationInvoiceDetail> findDetailsByIdIn(List<Long> detailIds) {
        return vendorQuotationDetailRepository.findByIdIn(detailIds);
    }

    public List<VendorQuotationInvoiceDetail> findDetailsByVendorQuotationInvoiceIdIn(Set<Long> quotationIds) {
        return vendorQuotationDetailRepository.findByVendorQuotationInvoiceIdIn(quotationIds);
    }

    public List<IqItemProjection> findDetailsByIdInForLogistic(Set<Long> detailIds, VendorRequestType type) {
        return vendorQuotationDetailRepository.findByIdInAndVendorRequestTypeForLogistic(detailIds, type);
    }

    public List<VendorQuotationInvoiceDetail> findByVendorQuotationInvoiceId(Long oldQuotationId) {
        return vendorQuotationDetailRepository.findByVendorQuotationInvoiceIdAndVendorRequestType(oldQuotationId, VendorRequestType.QUOTATION);
    }

    @Transactional
    public void updateQuantity(VqdQuantityDto vqdQuantityDto) {
        Set<Long> vqdIds = vqdQuantityDto.getVqdQuantities().stream().map(VqdQuantity::getId).collect(Collectors.toSet());
        Map<Long, VendorQuotationInvoiceDetail> vqDetailMap = getAllByDomainIdInUnfiltered(vqdIds).stream().collect(
                Collectors.toMap(VendorQuotationInvoiceDetail::getId, Function.identity()));
        List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetails = saveItemList(poUtilService.updateQuantity(vqDetailMap, vqdQuantityDto));
        updateDemandStatus(vendorQuotationInvoiceDetails,vqdQuantityDto);
    }

    private void updateDemandStatus(List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetails, VqdQuantityDto vqdQuantityDto) {
        PartOrder partOrder = partOrderService.findByIdUnfiltered(vqdQuantityDto.getPoId());
        if (partOrder.getInputType().equals(InputType.MANUAL)) {

            Set<Long> vendorQuotationInvoiceIds = vendorQuotationInvoiceDetails.stream().map(AbstractDomainBasedEntity::getId).collect(Collectors.toSet());
            List<IqItemProjection> iqItemProjections = findDetailsByIdInForLogistic(vendorQuotationInvoiceIds, VendorRequestType.QUOTATION);

            if (partOrder.getRfqType().equals(RfqType.PROCUREMENT)) {
                for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetails) {

                    demandStatusServiceImpl.deleteAllDemandStatusForPO(
                            vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            vendorQuotationInvoiceDetail.getId(),
                            getVoucherType(partOrder.getOrderType()));

                    demandStatusService.entityUpdateForPO(
                            Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart())? vendorQuotationInvoiceDetail.getAlternatePart().getId() :
                                    vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                            vendorQuotationInvoiceDetail.getRequisitionItem().getProcurementRequisition().getId(),
                            vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            vendorQuotationInvoiceDetail.getId(),
                            vendorQuotationInvoiceDetail.getPartQuantity(),
                            partOrder.getWorkFlowAction().getId(),
                            getVoucherType(partOrder.getOrderType()),
                            RfqType.PROCUREMENT.name(),
                            partOrder.getIsActive(),
                            partOrder.getInputType(),
                            partOrder.getIsRejected()
                    );
                }
            } else {
                for (IqItemProjection iqItemProjection : iqItemProjections) {

                    demandStatusServiceImpl.deleteAllDemandStatusForPO(
                            storeDemandDetailsService.findById(iqItemProjection.getDemandItemId()).getStoreDemand().getId(),
                            partOrder.getId(),
                            iqItemProjection.getId(),
                            getVoucherType(partOrder.getOrderType()));

                    demandStatusService.entityUpdateForPO(
                            iqItemProjection.getPartId(),
                            vqdQuantityDto.getPoId(),
                            storeDemandDetailsService.findById(iqItemProjection.getDemandItemId()).getStoreDemand().getId(),
                            partOrder.getId(),
                            iqItemProjection.getId(),
                            iqItemProjection.getPartQuantity(),
                            partOrder.getWorkFlowAction().getId(),
                            getVoucherType(partOrder.getOrderType()),
                            RfqType.LOGISTIC.name(),
                            partOrder.getIsActive(),
                            partOrder.getInputType(),
                            partOrder.getIsRejected()
                    );
                }
            }
        }else {

            if (partOrder.getRfqType().equals(RfqType.PROCUREMENT)) {
                List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetailList = partOrder.getPartOrderItemList().stream().map(PartOrderItem::getIqItem).collect(Collectors.toList());

                    for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetailList) {

                        demandStatusServiceImpl.deleteAllDemandStatusForPO(
                                vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                                partOrder.getId(),
                                vendorQuotationInvoiceDetail.getId(),
                                getVoucherType(partOrder.getOrderType()));

                        demandStatusService.entityUpdateForPO(
                                Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart()) ? vendorQuotationInvoiceDetail.getAlternatePart().getId() :
                                        vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                                partOrder.getCsDetail().getComparativeStatement().getId(),
                                vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                                partOrder.getId(),
                                vendorQuotationInvoiceDetail.getId(),
                                vendorQuotationInvoiceDetail.getPartQuantity(),
                                partOrder.getWorkFlowAction().getId(),
                                getVoucherType(partOrder.getOrderType()),
                                RfqType.PROCUREMENT.name(),
                                partOrder.getIsActive(),
                                partOrder.getInputType(),
                                partOrder.getIsRejected()
                        );
                    }
            } else {
                List<PartOrderItem> partOrderItemList = partOrderItemService.findByPartOrderId(partOrder.getId());

                for (PartOrderItem partOrderItem : partOrderItemList) {

                    demandStatusServiceImpl.deleteAllDemandStatusForPO(
                            partOrderItem.getIqItem().getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            partOrderItem.getIqItem().getId(),
                            getVoucherType(partOrder.getOrderType()));

                    demandStatusService.entityUpdateForPO(
                            Objects.nonNull(partOrderItem.getIqItem().getPoItem().getIqItem().getAlternatePart())?partOrderItem.getIqItem().getPoItem().getIqItem().getAlternatePart().getId():
                                    partOrderItem.getIqItem().getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId(),
                            partOrder.getCsDetail().getComparativeStatement().getId(),
                            partOrderItem.getIqItem().getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                            partOrder.getId(),
                            partOrderItem.getIqItem().getId(),
                            partOrderItem.getIqItem().getPartQuantity(),
                            partOrder.getWorkFlowAction().getId(),
                            getVoucherType(partOrder.getOrderType()),
                            RfqType.LOGISTIC.name(),
                            partOrder.getIsActive(),
                            partOrder.getInputType(),
                            partOrder.getIsRejected()
                    );
                }
            }
        }
    }

    private VoucherType getVoucherType(OrderType orderType) {
        VoucherType voucherType;
        switch (orderType)
        {
            case REPAIR:
                voucherType = VoucherType.RO;
                break;
            case LOAN:
                voucherType = VoucherType.LO;
                break;
            case EXCHANGE:
                voucherType = VoucherType.PO;
                break;
            case PURCHASE:
                voucherType = VoucherType.ORDER;
                break;
            default:
                voucherType = VoucherType.ORDER;
                break;
        }
        return voucherType;
    }

    public List<VqDetailProjection> findByVendorQuotationId(Long id) {
        return vendorQuotationDetailRepository.findVendorQuotationInvoiceDetailByVendorQuotationInvoiceIdAndVendorRequestType(
                id, VendorRequestType.QUOTATION);
    }

    public List<VendorQuotationInvoiceDetailViewModel> getAllVendorQuotationDetailByType(Long id, VendorRequestType vqType, RfqType rfqType) {
        List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetails =
                vendorQuotationDetailRepository.findByVendorQuotationInvoiceIdAndVendorRequestType(id, vqType);
        return getAllVendorQuotationDetail(vendorQuotationInvoiceDetails, rfqType);
    }

    public List<VendorQuotationInvoiceDetailViewModel> getAllVendorQuotationDetail(List<VendorQuotationInvoiceDetail> vendorQuotationDetailList,
                                                                                   RfqType rfqType) {
        Set<Long> partIds;
        Map<Long, Long> itemAndPartIdMap;
        if (rfqType == RfqType.LOGISTIC) {
            itemAndPartIdMap = new HashMap<>();

            List<PartOrderItemProjection> partOrderItemProjections = vendorQuotationUtil.getPoItemProjections(vendorQuotationDetailList);

            List<IqItemProjection> iqItemProjections = findDetailsByIdIn(
                    vendorQuotationUtil.getIqItemIds(partOrderItemProjections), VendorRequestType.QUOTATION);

            Map<Long, Long> iqItemAndPartId = vendorQuotationUtil.getIqItemPartIdMap(iqItemProjections);

            partIds = vendorQuotationUtil.getPartIds(iqItemProjections);

            partOrderItemProjections.forEach(poItem -> itemAndPartIdMap.put(poItem.getId(), iqItemAndPartId.get(poItem.getIqItemId())));
        } else {
            List<RequisitionItemProjection> requisitionItemProjections = vendorQuotationUtil.getReqItemProjections(vendorQuotationDetailList);

            partIds = vendorQuotationUtil.getPartIds(requisitionItemProjections, vendorQuotationDetailList);

            itemAndPartIdMap = CollectionUtils.isNotEmpty(requisitionItemProjections) ? requisitionItemProjections.stream().collect(Collectors.toMap(RequisitionItemProjection::getId,
                    RequisitionItemProjection::getDemandItemPartId)) : new HashMap<>();
        }

        Set<Long> storePartSerialIds = vendorQuotationDetailList.stream().map(VendorQuotationInvoiceDetail::getPartSerialId)
                .collect(Collectors.toSet());

        Set<Long> uomIds = vendorQuotationDetailList.stream().map(VendorQuotationInvoiceDetail::getUomId).collect(Collectors.toSet());

        Map<Long, UnitMeasurementProjection> unitMeasurementProjectionMap = unitMeasurementService.findByUnitMeasurementIdIn(uomIds).
                stream().collect(Collectors.toMap(UnitMeasurementProjection::getId, Function.identity()));

        List<StoreReturnPartDetailsProjection> storeReturnPartDetailsList = returnPartsDetailService.findReturnPartsDetailByRemovedPartSerialIdIn(storePartSerialIds);

        Map<Long, List<StoreReturnPartDetailsProjection>> srpdProjectionMap = storeReturnPartDetailsList.stream().
                collect(Collectors.groupingBy(StoreReturnPartDetailsProjection::getRemovedPartSerialId));

        Map<Long, PartProjection> partProjectionMap = partService.findPartByIdIn(partIds).stream().collect(Collectors.toMap(
                PartProjection::getId, Function.identity()));
        Set<Long> currencyIds = vendorQuotationDetailList.stream().map(VendorQuotationInvoiceDetail::getCurrencyId)
                .collect(Collectors.toSet());

        Map<Long, Currency> currencyMap = vendorQuotationUtil.getCurrencyMap(currencyIds);

        Set<Long> serialIds = vendorQuotationDetailList.stream().map(VendorQuotationInvoiceDetail::getPartSerialId).collect(Collectors.toSet());

        Map<Long, StorePartSerialProjection> spsProjectionMap = storePartSerialService.findStorePartSerialByIdIn(serialIds)
                .stream().collect(Collectors.toMap(StorePartSerialProjection::getId, Function.identity()));

        return vendorQuotationDetailList.stream().map(vendorQuotationDetail -> convertAllToResponseDto(
                vendorQuotationDetail,
                unitMeasurementProjectionMap.get(vendorQuotationDetail.getUomId()),
                vendorQuotationUtil.getCurrency(vendorQuotationDetail, currencyMap),
                vendorQuotationUtil.getPartProjection(vendorQuotationDetail, itemAndPartIdMap, partProjectionMap),
                vendorQuotationUtil.getAltPartProjection(vendorQuotationDetail, partProjectionMap),
                vendorQuotationUtil.getSpSerialProjection(vendorQuotationDetail, spsProjectionMap),
                vendorQuotationUtil.getReasonRemoved(vendorQuotationDetail, srpdProjectionMap),
                rfqType)).collect(Collectors.toList());
    }

    public List<VendorQuotationInvoiceDetail> createOrUpdateDetails(List<VendorQuotationInvoiceDetailDto> vendorQuotationInvoiceDetailDtoList,
                                                                    Long vendorQuotationInvoiceId,
                                                                    Long rfqOrPoId,
                                                                    VendorRequestType vendorRequestType,
                                                                    Long initialId,
                                                                    RfqType rfqType,
                                                                    InputType inputType) {
        Map<Long, VendorQuotationInvoiceDetail> vendorQuotationDetailMap = validateUpdate(vendorQuotationInvoiceDetailDtoList, initialId);

        Map<Long, ProcurementRequisitionItem> requisitionItemMap;
        Map<Long, PartOrderItem> partOrderItemMap;
        if (vendorRequestType == VendorRequestType.QUOTATION) {
            QuoteRequestProjection quoteRequestProjection = quoteRequestService.findQuoteRequestById(rfqOrPoId).get();
            if (rfqType == RfqType.LOGISTIC) {
                requisitionItemMap = new HashMap<>();
                partOrderItemMap = getPartOrderItemMap(inputType, vendorQuotationInvoiceDetailDtoList, quoteRequestProjection)
                        .stream().filter(Objects::nonNull).collect(Collectors.toMap(PartOrderItem::getId, Function.identity()));
            } else {
                partOrderItemMap = new HashMap<>();
                requisitionItemMap = getRequisitionItemMap(inputType, vendorQuotationInvoiceDetailDtoList, quoteRequestProjection)
                        .stream().filter(Objects::nonNull).collect(Collectors.toMap(ProcurementRequisitionItem::getId, Function.identity()));
            }
        } else {
            requisitionItemMap = new HashMap<>();
            partOrderItemMap = partOrderItemService.findByPartOrderId(rfqOrPoId).stream().filter(Objects::nonNull)
                    .collect(Collectors.toMap(PartOrderItem::getId, Function.identity()));
        }

        Set<Long> currencyIdList = vendorQuotationInvoiceDetailDtoList.stream().map(VendorQuotationInvoiceDetailDto::getCurrencyId)
                .collect(Collectors.toSet());

        Map<Long, Currency> currencyMap = vendorQuotationUtil.getCurrencyMap(currencyIdList);

        Set<Long> serialIds = vendorQuotationInvoiceDetailDtoList.stream().map(VendorQuotationInvoiceDetailDto::getPartSerialId)
                .collect(Collectors.toSet());

        Map<Long, StorePartSerial> storePartSerialMap = vendorQuotationUtil.getSerialMap(serialIds);

        Set<Long> alternatePartIds = vendorQuotationInvoiceDetailDtoList.stream().map(VendorQuotationInvoiceDetailDto::getAlternatePartId)
                .collect(Collectors.toSet());

        Map<Long, Part> alternatePartMap = partService.getAllByDomainIdIn(alternatePartIds, true).stream()
                .collect(Collectors.toMap(Part::getId, Function.identity()));

        Set<Long> partIds = vendorQuotationInvoiceDetailDtoList.stream().map(VendorQuotationInvoiceDetailDto::getPartId)
                .collect(Collectors.toSet());
        Map<Long, Part> partMap = partService.getAllByDomainIdIn(partIds, true).stream()
                .collect(Collectors.toMap(Part::getId, Function.identity()));

        Set<Long> uomIds = vendorQuotationInvoiceDetailDtoList.stream().map(VendorQuotationInvoiceDetailDto::getUomId).
                collect(Collectors.toSet());

        Map<Long, UnitMeasurement> unitMeasurementMap = unitMeasurementService.getAllByDomainIdIn(uomIds, true).stream().
                collect(Collectors.toMap(UnitMeasurement::getId, Function.identity()));

        List<VendorQuotationInvoiceDetail> vendorQuotationDetailList = vendorQuotationInvoiceDetailDtoList.stream()
                .map(iqDetailDto -> populateToEntity(
                        iqDetailDto,
                        unitMeasurementMap.get(iqDetailDto.getUomId()),
                        vendorQuotationDetailMap.getOrDefault(iqDetailDto.getId(), new VendorQuotationInvoiceDetail()),
                        vendorQuotationInvoiceId,
                        requisitionItemMap.get(iqDetailDto.getItemId()),
                        partOrderItemMap.get(iqDetailDto.getItemId()),
                        currencyMap.get(iqDetailDto.getCurrencyId()),
                        vendorRequestType,
                        storePartSerialMap.get(iqDetailDto.getPartSerialId()),
                        alternatePartMap.get(iqDetailDto.getAlternatePartId()),
                        partMap.get(iqDetailDto.getPartId())))
                .collect(Collectors.toList());

        return saveItemList(vendorQuotationDetailList);
    }

    @Override
    protected Specification<VendorQuotationInvoiceDetail> buildSpecification(IdQuerySearchDto searchDto) {
        return null;
    }

    @Override
    protected VendorQuotationInvoiceDetailViewModel convertToResponseDto(VendorQuotationInvoiceDetail vendorQuotationDetail) {
        //TODO: RFQ type should be dynamic!
        return getAllVendorQuotationDetail(Collections.singletonList(vendorQuotationDetail), RfqType.PROCUREMENT)
                .get(ApplicationConstant.FIRST_INDEX);
    }

    @Override
    protected VendorQuotationInvoiceDetail convertToEntity(VendorQuotationInvoiceDetailDto vendorQuotationInvoiceDetailDto) {
        return null;
    }

    @Override
    protected VendorQuotationInvoiceDetail updateEntity(VendorQuotationInvoiceDetailDto dto, VendorQuotationInvoiceDetail entity) {
        return null;
    }

    private VendorQuotationInvoiceDetail populateToEntity(VendorQuotationInvoiceDetailDto vendorQuotationInvoiceDetailDto,
                                                          UnitMeasurement unitMeasurement,
                                                          VendorQuotationInvoiceDetail vendorQuotationDetail,
                                                          Long vendorQuotationInvoiceId,
                                                          ProcurementRequisitionItem procurementRequisitionItem,
                                                          PartOrderItem partOrderItem,
                                                          Currency currency,
                                                          VendorRequestType vendorRequestType,
                                                          StorePartSerial partSerial,
                                                          Part alternatePart,
                                                          Part part) {

        vendorQuotationDetail.setVendorQuotationInvoiceId(vendorQuotationInvoiceId);
        vendorQuotationDetail.setVendorRequestType(vendorRequestType);
        vendorQuotationDetail.setUnitMeasurement(unitMeasurement);
        /** Requisition item for procurement */
        vendorQuotationDetail.setRequisitionItem(procurementRequisitionItem);
        /** Part order item for logistic and invoice */
        vendorQuotationDetail.setPoItem(partOrderItem);
        /** ----------------- */
        vendorQuotationDetail.setAlternatePart(alternatePart);
        vendorQuotationDetail.setCondition(vendorQuotationInvoiceDetailDto.getCondition());
        vendorQuotationDetail.setLeadTime(vendorQuotationInvoiceDetailDto.getLeadTime());
        vendorQuotationDetail.setIncoterms(vendorQuotationInvoiceDetailDto.getIncoterms());
        vendorQuotationDetail.setUnitPrice(vendorQuotationInvoiceDetailDto.getUnitPrice());
        vendorQuotationDetail.setExtendedPrice(vendorQuotationInvoiceDetailDto.getExtendedPrice());
        if (Objects.nonNull(vendorQuotationDetail.getUnitPrice()) || Objects.nonNull(vendorQuotationDetail.getExtendedPrice())) {
            if (Objects.isNull(currency)) {
                throw EngineeringManagementServerException.badRequest(ErrorId.CURRENCY_CODE_MUST_NOT_BE_EMPTY);
            }
            vendorQuotationDetail.setCurrency(currency);
        }
        vendorQuotationDetail.setPartQuantity(vendorQuotationInvoiceDetailDto.getPartQuantity());
        if (BooleanUtils.isFalse(vendorQuotationInvoiceDetailDto.getIsActive())) {
            vendorQuotationDetail.setIsActive(vendorQuotationInvoiceDetailDto.getIsActive());
        }
        if (vendorQuotationInvoiceDetailDto.getDiscount() > 0.0) {
            vendorQuotationDetail.setIsDiscount(Boolean.TRUE);
        }

        /** ------------ EXCHANGE TYPE ----------- */
        populateToMoqMovMlv(vendorQuotationDetail, vendorQuotationInvoiceDetailDto);
        populateToExchange(vendorQuotationDetail, vendorQuotationInvoiceDetailDto);
        populateToFlatRateExchange(vendorQuotationDetail, vendorQuotationInvoiceDetailDto);
        populateToRepair(vendorQuotationDetail, vendorQuotationInvoiceDetailDto);
        populateToLoan(vendorQuotationDetail, vendorQuotationInvoiceDetailDto);
        populateToCommonField(vendorQuotationDetail, vendorQuotationInvoiceDetailDto);

        if (Objects.nonNull(partSerial)) {
            vendorQuotationDetail.setPartSerial(partSerial);
        }
        vendorQuotationDetail.setDiscount(vendorQuotationInvoiceDetailDto.getDiscount());
        /*if (Objects.nonNull(part)) {
            partWiseUomService.updateAll(List.of(vendorQuotationInvoiceDetailDto.getUomId()), part, ApplicationConstant.OTHER);
        }*/
        return vendorQuotationDetail;
    }

    private void populateToCommonField(VendorQuotationInvoiceDetail vendorQuotationDetail, VendorQuotationInvoiceDetailDto vendorQuotationInvoiceDetailDto) {
        vendorQuotationDetail.setExchangeType(vendorQuotationInvoiceDetailDto.getExchangeType());
        vendorQuotationDetail.setAdditionalFeeType(vendorQuotationInvoiceDetailDto.getAdditionalFeeType());
        vendorQuotationDetail.setRaiScrapFee(vendorQuotationInvoiceDetailDto.getRaiScrapFee());
    }

    private void populateToLoan(VendorQuotationInvoiceDetail vendorQuotationDetail, VendorQuotationInvoiceDetailDto vendorQuotationInvoiceDetailDto) {
        vendorQuotationDetail.setLoanStatus(vendorQuotationInvoiceDetailDto.getLoanStatus());
        vendorQuotationDetail.setLoanStartDate(vendorQuotationInvoiceDetailDto.getLoanStartDate());
        vendorQuotationDetail.setLoanEndDate(vendorQuotationInvoiceDetailDto.getLoanEndDate());
    }

    private void populateToRepair(VendorQuotationInvoiceDetail vendorQuotationDetail, VendorQuotationInvoiceDetailDto vendorQuotationInvoiceDetailDto) {
        vendorQuotationDetail.setRepairType(vendorQuotationInvoiceDetailDto.getRepairType());
        vendorQuotationDetail.setTso(vendorQuotationInvoiceDetailDto.getTso());
        vendorQuotationDetail.setCso(vendorQuotationInvoiceDetailDto.getCso());
        vendorQuotationDetail.setTsn(vendorQuotationInvoiceDetailDto.getTsn());
        vendorQuotationDetail.setCsn(vendorQuotationInvoiceDetailDto.getCsn());
        vendorQuotationDetail.setTsr(vendorQuotationInvoiceDetailDto.getTsr());
        vendorQuotationDetail.setCsr(vendorQuotationInvoiceDetailDto.getCsr());
        vendorQuotationDetail.setEvaluationFee(vendorQuotationInvoiceDetailDto.getEvaluationFee());
    }

    private void populateToFlatRateExchange(VendorQuotationInvoiceDetail vendorQuotationDetail, VendorQuotationInvoiceDetailDto vendorQuotationInvoiceDetailDto) {
        vendorQuotationDetail.setBerLimit(vendorQuotationInvoiceDetailDto.getBerLimit());
    }

    private void populateToExchange(VendorQuotationInvoiceDetail vendorQuotationDetail, VendorQuotationInvoiceDetailDto vendorQuotationInvoiceDetailDto) {
        vendorQuotationDetail.setExchangeFee(vendorQuotationInvoiceDetailDto.getExchangeFee());
        vendorQuotationDetail.setRepairCost(vendorQuotationInvoiceDetailDto.getRepairCost());
    }

    private void populateToMoqMovMlv(VendorQuotationInvoiceDetail vendorQuotationDetail, VendorQuotationInvoiceDetailDto vendorQuotationInvoiceDetailDto) {
        vendorQuotationDetail.setMoq(vendorQuotationInvoiceDetailDto.getMoq());
        vendorQuotationDetail.setMov(vendorQuotationInvoiceDetailDto.getMov());
        vendorQuotationDetail.setMlv(vendorQuotationInvoiceDetailDto.getMlv());
    }

    private VendorQuotationInvoiceDetailViewModel convertAllToResponseDto(VendorQuotationInvoiceDetail vendorQuotationDetail,
                                                                          UnitMeasurementProjection unitMeasurementProjection,
                                                                          Currency currency,
                                                                          PartProjection partProjection,
                                                                          PartProjection alternatePartProjection,
                                                                          StorePartSerialProjection storePartSerialProjection,
                                                                          String reasonRemoved,
                                                                          RfqType rfqType) {

        VendorQuotationInvoiceDetailViewModel viewModel = new VendorQuotationInvoiceDetailViewModel();
        viewModel.setId(vendorQuotationDetail.getId());
        viewModel.setItemId(vendorQuotationUtil.getItemId(vendorQuotationDetail, rfqType));
        viewModel.setCondition(vendorQuotationDetail.getCondition());
        viewModel.setLeadTime(vendorQuotationDetail.getLeadTime());
        viewModel.setIncoterms(vendorQuotationDetail.getIncoterms());
        viewModel.setUnitPrice(vendorQuotationDetail.getUnitPrice());
        viewModel.setExtendedPrice(vendorQuotationDetail.getExtendedPrice());
        viewModel.setVendorRequestType(vendorQuotationDetail.getVendorRequestType());
        viewModel.setIsActive(vendorQuotationDetail.getIsActive());

        setUnitMeasurementData(unitMeasurementProjection, partProjection, alternatePartProjection, viewModel);

        if (nonNull(currency)) {
            viewModel.setCurrencyId(currency.getId());
            viewModel.setCurrencyCode(currency.getCode());
        }
        viewModel.setPartQuantity(vendorQuotationDetail.getPartQuantity());

        /** ------------ EXCHANGE TYPE ----------- */
        populateToMoqMovMlvResponse(vendorQuotationDetail, viewModel);
        populateToExchangeResponse(vendorQuotationDetail, viewModel);
        populateToFlatRateExchangeResponse(vendorQuotationDetail, viewModel);
        populateToRepairResponse(vendorQuotationDetail, viewModel, reasonRemoved);
        populateToLoanResponse(vendorQuotationDetail, viewModel);
        populateToCommonFieldResponse(vendorQuotationDetail, viewModel);

        if (Objects.nonNull(storePartSerialProjection)) {
            viewModel.setPartSerialId(storePartSerialProjection.getId());
            viewModel.setSerialId(storePartSerialProjection.getSerialId());
            viewModel.setSerialNo(storePartSerialProjection.getSerialSerialNumber());
        }
        viewModel.setDiscount(vendorQuotationDetail.getDiscount());
        viewModel.setIsDiscount(vendorQuotationDetail.getIsDiscount());
        viewModel.setVendorSerials(vendorQuotationDetail.getVendorSerials());

        return viewModel;
    }

    private void setUnitMeasurementData(UnitMeasurementProjection unitMeasurementProjection,
                                        PartProjection partProjection,
                                        PartProjection alternatePartProjection,
                                        VendorQuotationInvoiceDetailViewModel viewModel) {
        if (nonNull(unitMeasurementProjection)) {
            viewModel.setUnitMeasurementId(unitMeasurementProjection.getId());
            viewModel.setUnitMeasurementCode(unitMeasurementProjection.getCode());
        }
        if (nonNull(partProjection)) {
            viewModel.setPartId(partProjection.getId());
            viewModel.setPartNo(partProjection.getPartNo());
            viewModel.setPartDescription(partProjection.getDescription());
        }
        if (nonNull(alternatePartProjection)) {
            viewModel.setAlternatePartId(alternatePartProjection.getId());
            viewModel.setAlternatePartNo(alternatePartProjection.getPartNo());
            viewModel.setAlternatePartDescription(alternatePartProjection.getDescription());
        }
    }

    private void populateToCommonFieldResponse(VendorQuotationInvoiceDetail vendorQuotationDetail,
                                               VendorQuotationInvoiceDetailViewModel viewModel) {
        viewModel.setExchangeType(vendorQuotationDetail.getExchangeType());
        viewModel.setAdditionalFeeType(vendorQuotationDetail.getAdditionalFeeType());
        viewModel.setRaiScrapFee(vendorQuotationDetail.getRaiScrapFee());
    }

    private void populateToLoanResponse(VendorQuotationInvoiceDetail vendorQuotationDetail, VendorQuotationInvoiceDetailViewModel viewModel) {
        viewModel.setLoanStatus(vendorQuotationDetail.getLoanStatus());
        viewModel.setLoanStartDate(vendorQuotationDetail.getLoanStartDate());
        viewModel.setLoanEndDate(vendorQuotationDetail.getLoanEndDate());
    }

    private void populateToRepairResponse(VendorQuotationInvoiceDetail vendorQuotationDetail,
                                          VendorQuotationInvoiceDetailViewModel viewModel,
                                          String reasonRemoved) {
        viewModel.setReasonRemoved(reasonRemoved);
        viewModel.setRepairType(vendorQuotationDetail.getRepairType());
        viewModel.setTso(vendorQuotationDetail.getTso());
        viewModel.setCso(vendorQuotationDetail.getCso());
        viewModel.setTsn(vendorQuotationDetail.getTsn());
        viewModel.setCsn(vendorQuotationDetail.getCsn());
        viewModel.setTsr(vendorQuotationDetail.getTsr());
        viewModel.setCsr(vendorQuotationDetail.getCsr());
        viewModel.setEvaluationFee(vendorQuotationDetail.getEvaluationFee());
    }

    private void populateToFlatRateExchangeResponse(VendorQuotationInvoiceDetail vendorQuotationDetail,
                                                    VendorQuotationInvoiceDetailViewModel viewModel) {
        viewModel.setBerLimit(vendorQuotationDetail.getBerLimit());
    }

    private void populateToExchangeResponse(VendorQuotationInvoiceDetail vendorQuotationDetail,
                                            VendorQuotationInvoiceDetailViewModel viewModel) {
        viewModel.setExchangeFee(vendorQuotationDetail.getExchangeFee());
        viewModel.setRepairCost(vendorQuotationDetail.getRepairCost());
    }

    private void populateToMoqMovMlvResponse(VendorQuotationInvoiceDetail vendorQuotationDetail,
                                             VendorQuotationInvoiceDetailViewModel viewModel) {
        viewModel.setMoq(vendorQuotationDetail.getMoq());
        viewModel.setMov(vendorQuotationDetail.getMov());
        viewModel.setMlv(vendorQuotationDetail.getMlv());
    }

    private Map<Long, VendorQuotationInvoiceDetail> validateUpdate(List<VendorQuotationInvoiceDetailDto> vendorQuotationInvoiceDetailDtoList, Long initialId) {
        Set<Long> updateIdList = vendorQuotationInvoiceDetailDtoList.stream().map(VendorQuotationInvoiceDetailDto::getId)
                .collect(Collectors.toSet());

        return getAllByDomainIdIn(updateIdList, true).stream()
                .filter(vendorQuotationDetail -> {
                    if (!Objects.equals(vendorQuotationDetail.getVendorQuotationInvoiceId(), initialId)) {
                        throw EngineeringManagementServerException.badRequest(ErrorId.VALID_VENDOR_DETAIL_ID_REQUIRED);
                    }
                    return true;
                })
                .collect(Collectors.toMap(VendorQuotationInvoiceDetail::getId, Function.identity()));
    }

    private List<ProcurementRequisitionItem> getRequisitionItemMap(InputType inputType,
                                                                   List<VendorQuotationInvoiceDetailDto> detailDtos,
                                                                   QuoteRequestProjection quoteRequestProjection) {
        if (inputType == InputType.MANUAL) {
            return detailDtos.stream().map(vendorQuotationInvoiceDetailDto -> {
                        ProcurementRequisitionItem requisitionItem = vendorQuotationInvoiceDetailDto.getRequisitionItem();
                        if (Objects.nonNull(requisitionItem)) {
                            vendorQuotationInvoiceDetailDto.setItemId(requisitionItem.getId());
                        }
                        return requisitionItem;
                    })
                    .collect(Collectors.toList());
        } else {
            return requisitionItemService.findByRequisitionId(quoteRequestProjection.getRequisitionId());
        }
    }

    private List<PartOrderItem> getPartOrderItemMap(InputType inputType,
                                                    List<VendorQuotationInvoiceDetailDto> detailDtos,
                                                    QuoteRequestProjection quoteRequestProjection) {
        if (inputType == InputType.MANUAL) {
            return detailDtos.stream().map(vendorQuotationInvoiceDetailDto -> {
                        PartOrderItem partOrderItem = vendorQuotationInvoiceDetailDto.getPartOrderItem();
                        if (Objects.nonNull(partOrderItem)) {
                            vendorQuotationInvoiceDetailDto.setItemId(partOrderItem.getId());
                        }
                        return partOrderItem;
                    })
                    .collect(Collectors.toList());
        }
        return partOrderItemService.findByPartOrderId(quoteRequestProjection.getPartOrderId());
    }

    public  boolean existsByCurrencyIdAndIsActiveTrue(Long currencyId){
        return vendorQuotationDetailRepository.existsByCurrencyIdAndIsActiveTrue(currencyId);
    }

    public boolean existsByUomIdAndIsActiveTrue(Long uomId, Long partId) {
        return vendorQuotationDetailRepository.existsByUomIdAndRequisitionItemDemandItemPartIdAndIsActiveTrue(uomId, partId);
    }
}
