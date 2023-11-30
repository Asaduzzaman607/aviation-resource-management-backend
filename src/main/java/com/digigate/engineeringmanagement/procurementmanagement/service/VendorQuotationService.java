package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.VendorResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.VendorService;
import com.digigate.engineeringmanagement.procurementmanagement.constant.*;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.CsQuotationProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.QuoteRequestProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.QuoteRequestVendorProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.VqProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VendorQuotationDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VendorQuotationInvoiceDetailDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.VendorQuotationSearchDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.CsQuotationViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.PartOrderLiteDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.VendorQuotationInvoiceDetailViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.VendorQuotationViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartOrderItem;
import com.digigate.engineeringmanagement.procurementmanagement.entity.QuoteRequest;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotation;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotationInvoiceDetail;
import com.digigate.engineeringmanagement.procurementmanagement.repository.VendorQuotationRepository;
import com.digigate.engineeringmanagement.procurementmanagement.util.CommonUtil;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
import com.digigate.engineeringmanagement.status.serviceImpl.DemandStatusServiceImpl;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisitionItem;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.GenericAttachmentService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static com.digigate.engineeringmanagement.procurementmanagement.constant.InputType.CS;

@Service
public class VendorQuotationService extends AbstractSearchService<
        VendorQuotation,
        VendorQuotationDto,
        VendorQuotationSearchDto> {

    private final VendorQuotationRepository vendorQuotationRepository;
    private final QuoteRequestService quoteRequestService;
    private final VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService;
    private final VendorQuotationInvoiceFeeService vendorQuotationInvoiceFeeService;
    private final QuoteRequestVendorService quoteRequestVendorService;
    private final StoreVoucherTrackingService storeVoucherTrackingService;
    private final GenericAttachmentService genericAttachmentService;
    private final VendorService vendorService;
    private final DemandStatusService demandStatusService;
    private final DemandStatusServiceImpl demandStatusServiceImpl;
    private final CommonUtil commonUtil;

    public VendorQuotationService(VendorQuotationRepository vendorQuotationRepository,
                                  QuoteRequestService quoteRequestService,
                                  VendorQuotationInvoiceDetailService vendorQuotationInvoiceDetailService,
                                  VendorQuotationInvoiceFeeService vendorQuotationInvoiceFeeService,
                                  QuoteRequestVendorService quoteRequestVendorService,
                                  StoreVoucherTrackingService storeVoucherTrackingService,
                                  GenericAttachmentService genericAttachmentService, VendorService vendorService,
                                  DemandStatusService demandStatusService,
                                  DemandStatusServiceImpl demandStatusServiceImpl, CommonUtil commonUtil) {
        super(vendorQuotationRepository);
        this.vendorQuotationRepository = vendorQuotationRepository;
        this.quoteRequestService = quoteRequestService;
        this.quoteRequestVendorService = quoteRequestVendorService;
        this.vendorQuotationInvoiceDetailService = vendorQuotationInvoiceDetailService;
        this.vendorQuotationInvoiceFeeService = vendorQuotationInvoiceFeeService;
        this.storeVoucherTrackingService = storeVoucherTrackingService;
        this.genericAttachmentService = genericAttachmentService;
        this.vendorService = vendorService;
        this.demandStatusService = demandStatusService;
        this.demandStatusServiceImpl = demandStatusServiceImpl;
        this.commonUtil = commonUtil;
    }

    public VendorQuotation findByPartOrderId(Long partOrderId) {
        return vendorQuotationRepository.findByPartOrderId(partOrderId);
    }

    public List<CsQuotationProjection> findVendorQuotationByIdIn(Set<Long> quotationIdList) {
        return vendorQuotationRepository.findVendorQuotationByIdIn(quotationIdList);
    }

    public List<VqProjection> findVQByIdIn(Set<Long> quoteIdSet) {
        return vendorQuotationRepository.findByIdIn(quoteIdSet);
    }

    public List<VendorQuotation> finByPartOrderIdIn(Set<Long> ids) {
        return vendorQuotationRepository.findByPartOrderIdIn(ids);
    }

    @Transactional
    @Override
    public VendorQuotation create(VendorQuotationDto vendorQuotationDto) {
        return create(vendorQuotationDto, OVERLOAD).getLeft();
    }

    public Pair<VendorQuotation, List<VendorQuotationInvoiceDetail>> create(VendorQuotationDto vendorQuotationDto, Long overload) {
        if (vendorQuotationRepository.findByQuoteRequestIdAndQuoteRequestVendorIdAndIsActiveTrue(
                vendorQuotationDto.getQuoteRequestId(), vendorQuotationDto.getQuoteRequestVendorId()).stream().findFirst().isPresent()) {
            throw EngineeringManagementServerException.badRequest(ErrorId.VENDOR_MUST_NOT_BE_DUPLICATE_UNDER_SAME_RFQ);
        }

        VendorQuotation vendorQuotation = convertToEntity(vendorQuotationDto);

        /** Need to set id for Manual PO */
        if (vendorQuotation.getInputType() == InputType.MANUAL) {
            vendorQuotationDto.setQuoteRequestId(vendorQuotationDto.getQuoteRequest().getId());
            vendorQuotationDto.setQuoteRequestVendorId(vendorQuotationDto.getQuoteRequestVendor().getId());
        }
        vendorQuotation = super.saveItem(vendorQuotation);

        List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetails = vendorQuotationInvoiceDetailService.createOrUpdateDetails(
                vendorQuotationDto.getVendorQuotationDetails(), vendorQuotation.getId(), vendorQuotationDto.getQuoteRequestId(),
                VendorRequestType.QUOTATION, null, vendorQuotationDto.getRfqType(), vendorQuotationDto.getInputType());

        if (!CollectionUtils.isEmpty(vendorQuotationDto.getVendorQuotationFees())) {
            vendorQuotationInvoiceFeeService.createOrUpdateFees(vendorQuotationDto.getVendorQuotationFees(), vendorQuotation.getId(),
                    VendorRequestType.QUOTATION, null);
        }
        genericAttachmentService.saveAllAttachments(vendorQuotationDto.getAttachments(), FeatureName.QUOTATION, vendorQuotation.getId());

        if (vendorQuotation.getRfqType().equals(RfqType.PROCUREMENT)) {
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetails) {
                demandStatusService.createWithVQDetailsId(
                        Objects.nonNull(vendorQuotationInvoiceDetail.getAlternatePart()) ? vendorQuotationInvoiceDetail.getAlternatePart().getId() :
                                vendorQuotationInvoiceDetail.getRequisitionItem().getDemandItem().getPart().getId(),
                        vendorQuotation.getQuoteRequest().getId(),
                        vendorQuotation.getQuoteRequest().getProcurementRequisition().getStoreDemand().getId(),
                        vendorQuotation.getId(),
                        vendorQuotationInvoiceDetail.getId(),
                        vendorQuotationInvoiceDetail.getPartQuantity(),
                        null,
                        VoucherType.QUOTE,
                        RfqType.PROCUREMENT.name());
            }
        } else {
            for (VendorQuotationInvoiceDetail vendorQuotationInvoiceDetail : vendorQuotationInvoiceDetails) {
                demandStatusService.createWithVQDetailsId(
                        vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getPart().getId(),
                        vendorQuotation.getQuoteRequest().getId(),
                        vendorQuotationInvoiceDetail.getPoItem().getIqItem().getRequisitionItem().getDemandItem().getStoreDemand().getId(),
                        vendorQuotation.getId(),
                        vendorQuotationInvoiceDetail.getId(),
                        vendorQuotationInvoiceDetail.getPartQuantity(),
                        null,
                        VoucherType.QUOTE,
                        RfqType.LOGISTIC.name());
            }
        }
        return Pair.of(vendorQuotation, vendorQuotationInvoiceDetails);
    }

    private Long getDemandId(VendorQuotation vendorQuotation) {
        if (vendorQuotation.getQuoteRequest().getPartOrder().getVoucherNo().contains("PO") || vendorQuotation.getQuoteRequest().getPartOrder().getVoucherNo().contains("ORDER")
        ||  vendorQuotation.getQuoteRequest().getPartOrder().getVoucherNo().contains("USBA")) {
            return vendorQuotation.getQuoteRequest().getPartOrder().getPartOrderItemList()
                    .stream().map(PartOrderItem::getIqItem).collect(Collectors.toList()).stream()
                    .findFirst().get().getRequisitionItem()
                    .getDemandItem().getStoreDemand().getId();
        } else {
            return vendorQuotation.getQuoteRequest().getPartOrder().getCsDetail()
                    .getComparativeStatement().getQuoteRequest().getProcurementRequisition().getStoreDemand().getId();
        }

    }


    @Transactional
    @Override
    public VendorQuotation update(VendorQuotationDto vendorQuotationDto, Long id) {
        return update(vendorQuotationDto, id, OVERLOAD).getLeft();
    }

    public Pair<VendorQuotation, List<VendorQuotationInvoiceDetail>> update(VendorQuotationDto vendorQuotationDto, Long id, Long overload) {
        VendorQuotation vendorQuotation = super.update(vendorQuotationDto, id);

        List<VendorQuotationInvoiceDetail> vendorQuotationInvoiceDetails = vendorQuotationInvoiceDetailService.createOrUpdateDetails(
                vendorQuotationDto.getVendorQuotationDetails(), vendorQuotation.getId(), vendorQuotationDto.getQuoteRequestId(),
                VendorRequestType.QUOTATION, id, vendorQuotationDto.getRfqType(), vendorQuotationDto.getInputType());

        if (!CollectionUtils.isEmpty(vendorQuotationDto.getVendorQuotationFees())) {
            vendorQuotationInvoiceFeeService.createOrUpdateFees(vendorQuotationDto.getVendorQuotationFees(), vendorQuotation.getId(),
                    VendorRequestType.QUOTATION, id);
        }

        genericAttachmentService.updateByRecordId(FeatureName.QUOTATION, id, vendorQuotationDto.getAttachments());

        boolean isDeleted = true;
        if (vendorQuotation.getRfqType().equals(RfqType.PROCUREMENT)) {
            for (VendorQuotationInvoiceDetailDto vendorQuotationInvoiceDetailDto : vendorQuotationDto.getVendorQuotationDetails()) {
                if (isDeleted) {
                    demandStatusServiceImpl.deleteAllDemandStatus(
                            Objects.nonNull(vendorQuotation.getQuoteRequest().getProcurementRequisition()) ?
                                    vendorQuotation.getQuoteRequest().getProcurementRequisition().getStoreDemand().getId() : null,
                            vendorQuotation.getId(),
                            VoucherType.QUOTE);
                    isDeleted = false;
                }
                demandStatusService.entityUpdateWithVQDetailsId(
                        Objects.nonNull(vendorQuotationInvoiceDetailDto.getAlternatePartId()) ? vendorQuotationInvoiceDetailDto.getAlternatePartId() :
                                Objects.nonNull(vendorQuotation.getQuoteRequest().getProcurementRequisition()) ? vendorQuotation.getQuoteRequest().getProcurementRequisition()
                                        .getProcurementRequisitionItems().stream().map(ProcurementRequisitionItem::getDemandItem).collect(Collectors.toList())
                                        .stream().map(StoreDemandItem::getPart)
                                        .collect(Collectors.toList()).stream().findFirst().get().getId() : null,
                        vendorQuotation.getQuoteRequest().getId(),
                        Objects.nonNull(vendorQuotation.getQuoteRequest().getProcurementRequisition()) ?
                                vendorQuotation.getQuoteRequest().getProcurementRequisition().getStoreDemand().getId() : null,
                        vendorQuotation.getId(),
                        vendorQuotationInvoiceDetailDto.getId(),
                        vendorQuotationInvoiceDetailDto.getPartQuantity(),
                        null,
                        VoucherType.QUOTE,
                        RfqType.PROCUREMENT.name());
            }
        } else {
            for (VendorQuotationInvoiceDetailDto vendorQuotationInvoiceDetailDto : vendorQuotationDto.getVendorQuotationDetails()) {
                if (isDeleted) {
                    demandStatusServiceImpl.deleteAllDemandStatus(
                            vendorQuotation.getQuoteRequest().getPartOrder().getPartOrderItemList()
                                    .stream().map(PartOrderItem::getIqItem).collect(Collectors.toList()).
                                    stream().map(VendorQuotationInvoiceDetail::getRequisitionItem).collect(Collectors.toList())
                                    .stream().map(ProcurementRequisitionItem::getDemandItem).collect(Collectors.toList()).stream().map(StoreDemandItem::getStoreDemand)
                                    .collect(Collectors.toList()).stream().findFirst().get().getId(),
                            vendorQuotation.getId(),
                            VoucherType.QUOTE);
                    isDeleted = false;
                }
                demandStatusService.entityUpdateWithVQDetailsId(
                        Objects.nonNull(vendorQuotationInvoiceDetailDto.getAlternatePartId()) ? vendorQuotationInvoiceDetailDto.getAlternatePartId() :
                                vendorQuotationInvoiceDetailDto.getPartId(),
                        vendorQuotation.getQuoteRequest().getId(),
                        getDemandId(vendorQuotation),
                        vendorQuotation.getId(),
                        vendorQuotationInvoiceDetailDto.getId(),
                        vendorQuotationInvoiceDetailDto.getPartQuantity(),
                        null,
                        VoucherType.QUOTE,
                        RfqType.LOGISTIC.name());
            }
        }
        return Pair.of(vendorQuotation, vendorQuotationInvoiceDetails);
    }

    @Override
    public PageData search(VendorQuotationSearchDto searchDto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Page<VendorQuotation> pagedData = vendorQuotationRepository.findAll(
                buildSpecification(searchDto), pageable);

        return PageData.builder()
                .model(getAllResponse(pagedData.getContent()))
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    public Pair<String, List<CsQuotationViewModel>> findVendorQuotationByType(OrderType type, Long rfqId) {
        List<CsQuotationViewModel> csQuotationViewModels = new ArrayList<>();
        if (OrderType.PURCHASE == type) {
            csQuotationViewModels.addAll(vendorQuotationRepository.findByCriteria(ExchangeType.PURCHASE, rfqId));
        } else if (OrderType.REPAIR == type) {
            csQuotationViewModels.addAll(vendorQuotationRepository.findByCriteria(ExchangeType.REPAIR, rfqId));
        } else if (OrderType.LOAN == type) {
            csQuotationViewModels.addAll(vendorQuotationRepository.findByCriteria(ExchangeType.LOAN, rfqId));
        } else if (OrderType.EXCHANGE == type) {
            csQuotationViewModels.addAll(vendorQuotationRepository.findByCriteria(ExchangeType.EXCHANGE_WITH_COST, rfqId));
            csQuotationViewModels.addAll(vendorQuotationRepository.findByCriteria(ExchangeType.FLAT_RATE_EXCHANGE_WITH_BER_LIMIT, rfqId));
            csQuotationViewModels.addAll(vendorQuotationRepository.findByCriteria(ExchangeType.FLAT_RATE_EXCHANGE_WITH_NO_BILL_BACK, rfqId));
        } else {
            return Pair.of(MODEL, new ArrayList<>());
        }

        return Pair.of(MODEL, csQuotationViewModels.stream().filter(quotation ->
                !commonUtil.isInvisible(quotation.getQuotationNo())).collect(Collectors.toList()));
    }

    @Override
    protected Specification<VendorQuotation> buildSpecification(VendorQuotationSearchDto searchDto) {
        CustomSpecification<VendorQuotation> specification = new CustomSpecification<>();

        return Specification.where(specification.equalSpecificationAtRoot(searchDto.getIsActive(), IS_ACTIVE_FIELD)
                .and(specification.likeSpecificationAtRoot(searchDto.getQuery(), QUOTATION_NO))
                .and(specification.equalSpecificationAtRoot(searchDto.getRfqType(), RFQ_TYPE))
                .and(specification.equalSpecificationAtRoot(searchDto.getInputType(), INPUT_TYPE))
                .and(specification.equalSpecificationAtRoot(searchDto.getRfqId(), QUOTE_REQUEST_ID))
                .and(specification.likeSpecificationAtRoot(VoucherType.QUOTE.name(), QUOTATION_NO)));
    }

    @Override
    protected VendorQuotationViewModel convertToResponseDto(VendorQuotation vendorQuotation) {
        return getAllResponse(Collections.singletonList(vendorQuotation)).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));
    }

    public VendorResponseDto convertToVendorResponseDto(VendorQuotation vendorQuotation) {
        return getVendorResponse(Collections.singletonList(vendorQuotation)).stream().findFirst().orElseThrow(() ->
                EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));
    }

    private List<VendorResponseDto> getVendorResponse(List<VendorQuotation> vendorQuotationList) {

        Set<Long> qrVendorIds = vendorQuotationList.stream().filter(vendorQuotation -> Objects.nonNull(vendorQuotation) && Objects.equals(
                vendorQuotation.getInputType(), CS)).map(VendorQuotation::getQuoteRequestVendorId).collect(Collectors.toSet());

        Map<Long, QuoteRequestVendorProjection> quoteRequestVendorProjectionMap = quoteRequestVendorService.findQuoteRequestVendorByIdIn(qrVendorIds)
                .stream().collect(Collectors.toMap(QuoteRequestVendorProjection::getId, Function.identity()));

        Set<Long> quoteRequestVendorIds = vendorQuotationList.stream().filter(vendorQuotation -> Objects.nonNull(vendorQuotation) && Objects.equals(
                vendorQuotation.getInputType(), InputType.MANUAL)).map(VendorQuotation::getQuoteRequestVendorId).collect(Collectors.toSet());
        Set<Long> vendorIds = quoteRequestVendorService.findQuoteRequestVendorByIdIn(quoteRequestVendorIds).stream()
                .map(QuoteRequestVendorProjection::getVendorId).collect(Collectors.toSet());
        Map<Long, Vendor> vendorMap = vendorService.getAllByDomainIdIn(vendorIds, true).stream().collect(Collectors.toMap(Vendor::getId, Function.identity()));

        return vendorQuotationList.stream().filter(Objects::nonNull).map(vendorQuotation -> populateToVendorViewModel(vendorQuotation, quoteRequestVendorProjectionMap,
                vendorMap)).collect(Collectors.toList());
    }

    public VendorQuotationViewModel convertToResponseDto(VendorQuotation vendorQuotation, Set<Long> itemIds) {
        VendorQuotationViewModel vendorQuotationViewModel = convertToResponseDto(vendorQuotation);
        List<VendorQuotationInvoiceDetailViewModel> vendorQuotationDetails = vendorQuotationViewModel.getVendorQuotationDetails();

        vendorQuotationViewModel.setVendorQuotationDetails(vendorQuotationDetails.stream().filter(
                vqd -> itemIds.contains(vqd.getItemId())).collect(Collectors.toList()));
        return vendorQuotationViewModel;
    }

    @Override
    protected VendorQuotation convertToEntity(VendorQuotationDto vendorQuotationDto) {
        return populateToEntity(vendorQuotationDto, new VendorQuotation());
    }

    @Override
    protected VendorQuotation updateEntity(VendorQuotationDto dto, VendorQuotation entity) {
        return populateToEntity(dto, entity);
    }

    private VendorQuotation populateToEntity(VendorQuotationDto vendorQuotationDto, VendorQuotation vendorQuotation) {

        if (vendorQuotationDto.getInputType() == InputType.MANUAL
                && Objects.nonNull(vendorQuotationDto.getQuoteRequest())) {
            vendorQuotation.setQuoteRequest(vendorQuotationDto.getQuoteRequest());

            /** Setting hidden quotation pattern */
            vendorQuotation.setQuotationNo(INVISIBLE + ZonedDateTime.now().toInstant().toEpochMilli());

        } else if (Objects.nonNull(vendorQuotationDto.getQuoteRequestId())
                && !vendorQuotationDto.getQuoteRequestId().equals(vendorQuotation.getQuoteRequestId())) {
            QuoteRequest quoteRequest = quoteRequestService.findById(vendorQuotationDto.getQuoteRequestId());
            vendorQuotation.setQuoteRequest(quoteRequest);

            /** Setting quotation pattern */
            vendorQuotation.setQuotationNo(storeVoucherTrackingService.generateUniqueVoucherNo(vendorQuotationDto.getQuoteRequestId(),
                    VoucherType.QUOTE, quoteRequest.getRfqNo()));
        }
        vendorQuotation.setDate(vendorQuotationDto.getDate());
        vendorQuotation.setValidUntil(vendorQuotationDto.getValidUntil());

        if (vendorQuotationDto.getInputType() == InputType.MANUAL
                && Objects.nonNull(vendorQuotationDto.getQuoteRequestVendor())) {
            vendorQuotation.setQuoteRequestVendor(vendorQuotationDto.getQuoteRequestVendor());
        } else if (Objects.nonNull(vendorQuotationDto.getQuoteRequestVendorId()) &&
                !vendorQuotationDto.getQuoteRequestVendorId().equals(vendorQuotation.getQuoteRequestVendorId())) {
            vendorQuotation.setQuoteRequestVendor(
                    quoteRequestVendorService.findById(vendorQuotationDto.getQuoteRequestVendorId()));
        }

        vendorQuotation.setVendorAddress(vendorQuotationDto.getVendorAddress());
        vendorQuotation.setVendorEmail(vendorQuotationDto.getVendorEmail());
        vendorQuotation.setVendorTel(vendorQuotationDto.getVendorTel());
        vendorQuotation.setVendorFax(vendorQuotationDto.getVendorFax());
        vendorQuotation.setVendorWebsite(vendorQuotationDto.getVendorWebsite());
        vendorQuotation.setVendorFrom(vendorQuotationDto.getVendorFrom());
        vendorQuotation.setVendorQuotationNo(vendorQuotationDto.getVendorQuotationNo());
        vendorQuotation.setToAttention(vendorQuotationDto.getToAttention());
        vendorQuotation.setToFax(vendorQuotationDto.getToFax());
        vendorQuotation.setToTel(vendorQuotationDto.getToTel());
        vendorQuotation.setRemark(vendorQuotationDto.getRemark());
        vendorQuotation.setQuoteStatus(vendorQuotationDto.getQuoteStatus());
        vendorQuotation.setTermsCondition(vendorQuotationDto.getTermsCondition());
        vendorQuotation.setRfqType(vendorQuotationDto.getRfqType());
        vendorQuotation.setInputType(vendorQuotationDto.getInputType());

        return vendorQuotation;
    }

    private VendorResponseDto populateToVendorViewModel(VendorQuotation vendorQuotation,
                                                        Map<Long, QuoteRequestVendorProjection> quoteRequestVendorProjectionMap,
                                                        Map<Long, Vendor> vendorMap) {

        VendorResponseDto vendorResponseDto = new VendorResponseDto();
        Long vendorId = vendorQuotation.getQuoteRequestVendor().getVendorId();
        if (Objects.equals(vendorQuotation.getInputType(), InputType.MANUAL) && Objects.nonNull(vendorId)) {
            Vendor vendor = vendorMap.get(vendorId);
            if (Objects.nonNull(vendor)) {
                vendorInfoView(vendorResponseDto, vendor);
            }
        } else if (Objects.equals(vendorQuotation.getInputType(), CS) && Objects.nonNull(vendorQuotation.getQuoteRequestVendorId())) {
            QuoteRequestVendorProjection qrvProjection = quoteRequestVendorProjectionMap.get(vendorQuotation.getQuoteRequestVendorId());
            if (Objects.nonNull(qrvProjection)) {
                vendorInfoView(vendorResponseDto, qrvProjection);
            }
        }

        return vendorResponseDto;
    }

    private VendorQuotationViewModel populateToViewModel(VendorQuotation vendorQuotation,
                                                         Map<Long, QuoteRequestProjection> quoteRequestProjectionMap,
                                                         Map<Long, QuoteRequestVendorProjection> quoteRequestVendorProjectionMap) {

        QuoteRequestProjection quoteRequestProjection = quoteRequestProjectionMap.get(vendorQuotation.getQuoteRequestId());

        VendorQuotationViewModel vendorQuotationViewModel = new VendorQuotationViewModel();
        vendorQuotationViewModel.setId(vendorQuotation.getId());
        if (Objects.nonNull(quoteRequestProjection)) {
            vendorQuotationViewModel.setQuoteRequestId(quoteRequestProjection.getId());

            /** Added Manual check */
            vendorQuotationViewModel.setQuoteRequestNo(quoteRequestProjection.getInputType() == CS ?
                    quoteRequestProjection.getRfqNo() : StringUtils.EMPTY);

            vendorQuotationViewModel.setQuoteRequestDate(quoteRequestProjection.getUpdateDate());
            vendorQuotationViewModel.setParentOrder(PartOrderLiteDto.of(quoteRequestProjection.getPartOrderId(),
                    quoteRequestProjection.getPartOrderOrderNo()));
        }
        vendorQuotationViewModel.setQuoteRequestVendorId(vendorQuotation.getQuoteRequestVendorId());

        if (Objects.nonNull(vendorQuotation.getQuoteRequestVendorId())) {
            QuoteRequestVendorProjection qrvProjection = quoteRequestVendorProjectionMap.get(vendorQuotation.getQuoteRequestVendorId());
            if (Objects.nonNull(qrvProjection)) {
                vendorQuotationViewModel.setVendorId(qrvProjection.getVendorId());
                vendorQuotationViewModel.setVendorName(qrvProjection.getVendorName());
                vendorQuotationViewModel.setVendorType(qrvProjection.getVendorVendorType());
            }
        }
        vendorQuotationViewModel.setTermsCondition(vendorQuotation.getTermsCondition());

        /** Added Manual check */
        vendorQuotationViewModel.setQuotationNo(commonUtil.isInvisible(vendorQuotation.getQuotationNo()) ?
                StringUtils.EMPTY : vendorQuotation.getQuotationNo());

        vendorQuotationViewModel.setDate(vendorQuotation.getDate());
        vendorQuotationViewModel.setValidUntil(vendorQuotation.getValidUntil());
        vendorQuotationViewModel.setVendorAddress(vendorQuotation.getVendorAddress());
        vendorQuotationViewModel.setVendorEmail(vendorQuotation.getVendorEmail());
        vendorQuotationViewModel.setVendorTel(vendorQuotation.getVendorTel());
        vendorQuotationViewModel.setVendorFax(vendorQuotation.getVendorFax());
        vendorQuotationViewModel.setVendorWebsite(vendorQuotation.getVendorWebsite());
        vendorQuotationViewModel.setVendorFrom(vendorQuotation.getVendorFrom());

        /** Bypassed data for vendor quotation no repeat */
        vendorQuotationViewModel.setVendorQuotationNo(vendorQuotationViewModel.getQuotationNo());

        vendorQuotationViewModel.setToAttention(vendorQuotation.getToAttention());
        vendorQuotationViewModel.setToFax(vendorQuotation.getToFax());
        vendorQuotationViewModel.setToTel(vendorQuotation.getToTel());
        vendorQuotationViewModel.setAttachments(
                genericAttachmentService.getLinksByFeatureNameAndId(FeatureName.QUOTATION, vendorQuotation.getId()));
        vendorQuotationViewModel.setRemark(vendorQuotation.getRemark());
        vendorQuotationViewModel.setQuoteStatus(vendorQuotation.getQuoteStatus());
        vendorQuotationViewModel.setRfqType(vendorQuotation.getRfqType());
        vendorQuotationViewModel.setInputType(vendorQuotation.getInputType());

        vendorQuotationViewModel.setVendorQuotationDetails(vendorQuotationInvoiceDetailService.getAllVendorQuotationDetailByType(
                vendorQuotation.getId(), VendorRequestType.QUOTATION, vendorQuotation.getRfqType()));
        vendorQuotationViewModel.setVendorQuotationFees(vendorQuotationInvoiceFeeService.getAllVendorQuotationFeeByType(
                vendorQuotation.getId(), VendorRequestType.QUOTATION));

        return vendorQuotationViewModel;
    }

    private void vendorInfoView(VendorResponseDto vendorResponseDto, Vendor vendor) {
        vendorResponseDto.setName(vendor.getName());
        vendorResponseDto.setAddress(vendor.getAddress());
        vendorResponseDto.setOfficePhone(vendor.getOfficePhone());
        vendorResponseDto.setEmail(vendor.getEmail());
        vendorResponseDto.setWebsite(vendor.getWebsite());
        vendorResponseDto.setContactPerson(vendor.getContactPerson());
        vendorResponseDto.setContactSkype(vendor.getContactSkype());
    }

    private void vendorInfoView(VendorResponseDto vendorResponseDto, QuoteRequestVendorProjection qrvProjection) {
        vendorResponseDto.setName(qrvProjection.getVendorName());
        vendorResponseDto.setAddress(qrvProjection.getVendorAddress());
        vendorResponseDto.setOfficePhone(qrvProjection.getVendorOfficePhone());
        vendorResponseDto.setEmail(qrvProjection.getVendorEmail());
        vendorResponseDto.setWebsite(qrvProjection.getVendorWebsite());
        vendorResponseDto.setContactPerson(qrvProjection.getVendorContactPerson());
        vendorResponseDto.setContactSkype(qrvProjection.getVendorContactSkype());
    }

    private List<VendorQuotationViewModel> getAllResponse(List<VendorQuotation> vendorQuotationList) {
        Set<Long> quotationRequestIds = vendorQuotationList.stream().filter(Objects::nonNull).map(VendorQuotation::getQuoteRequestId).collect(Collectors.toSet());

        Map<Long, QuoteRequestProjection> quoteRequestProjectionMap = quoteRequestService.findQuoteRequestByIdIn(quotationRequestIds)
                .stream().collect(Collectors.toMap(QuoteRequestProjection::getId, Function.identity()));

        Set<Long> qrVendorIds = vendorQuotationList.stream().filter(Objects::nonNull).map(VendorQuotation::getQuoteRequestVendorId).collect(Collectors.toSet());

        Map<Long, QuoteRequestVendorProjection> quoteRequestVendorProjectionMap = quoteRequestVendorService.findQuoteRequestVendorByIdIn(qrVendorIds)
                .stream().collect(Collectors.toMap(QuoteRequestVendorProjection::getId, Function.identity()));

        return vendorQuotationList.stream().filter(Objects::nonNull).map(vendorQuotation -> populateToViewModel(vendorQuotation,
                quoteRequestProjectionMap, quoteRequestVendorProjectionMap)).collect(Collectors.toList());
    }

    public List<VendorQuotation> findByQuoteRequestId(Long rfqId) {
        return vendorQuotationRepository.findByQuoteRequestId(rfqId);
    }
}