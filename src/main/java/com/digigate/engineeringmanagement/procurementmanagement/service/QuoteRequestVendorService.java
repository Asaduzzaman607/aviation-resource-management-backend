package com.digigate.engineeringmanagement.procurementmanagement.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.VendorService;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.QuoteRequestVendorProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.QuoteRequestVendorDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.QuoteRequestVendorSearchDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.request.RfqRequestDto;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.QrVendorViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.QuoteRequestViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.RfqVendorResponseDto;
import com.digigate.engineeringmanagement.procurementmanagement.entity.QuoteRequest;
import com.digigate.engineeringmanagement.procurementmanagement.entity.QuoteRequestVendor;
import com.digigate.engineeringmanagement.procurementmanagement.repository.QuoteRequestVendorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.IS_ACTIVE_FIELD;

/**
 * Quote Request Vendor Service
 *
 * @author Sayem Hasnat
 */
@Service
public class QuoteRequestVendorService extends AbstractSearchService<
        QuoteRequestVendor, QuoteRequestVendorDto, QuoteRequestVendorSearchDto> {

    private final VendorService vendorService;
    private final QuoteRequestVendorRepository quoteRequestVendorRepository;

    public QuoteRequestVendorService(AbstractRepository<QuoteRequestVendor> repository,
                                     VendorService vendorService,
                                     QuoteRequestVendorRepository quoteRequestVendorRepository) {
        super(repository);
        this.vendorService = vendorService;
        this.quoteRequestVendorRepository = quoteRequestVendorRepository;
    }

    public List<QuoteRequestVendorProjection> findQuoteRequestVendorByIdIn(Set<Long> quoteRequestVendorIdList) {
        return quoteRequestVendorRepository.findQuoteRequestVendorByIdIn(quoteRequestVendorIdList);
    }

    public List<QuoteRequestVendor> findByQuoteRequestId(Long rfqId) {
        return quoteRequestVendorRepository.findByQuoteRequestId(rfqId);
    }

    /** This method is responsible to convert Entity to Response view Model
     *
     * @param quoteRequestVendor          {@link QuoteRequestVendor}
     * @return                            {@link QrVendorViewModel}
     */
    @Override
    protected QrVendorViewModel convertToResponseDto(QuoteRequestVendor quoteRequestVendor) {
        QrVendorViewModel QRVendorViewModel = new QrVendorViewModel();
        QRVendorViewModel.setId(quoteRequestVendor.getId());
        QRVendorViewModel.setQuoteRequestId(quoteRequestVendor.getQuoteRequestId());
        QRVendorViewModel.setRequestDate(quoteRequestVendor.getRequestDate());
        QRVendorViewModel.setVendorId(quoteRequestVendor.getVendorId());
        QRVendorViewModel.setVendorType(quoteRequestVendor.getVendor().getVendorType());
        QRVendorViewModel.setVendorName(quoteRequestVendor.getVendor().getName());
        return QRVendorViewModel;
    }

    /** This method will convert QuoteRequestVendorDto to QuoteRequestVendor
     * @param quoteRequestVendorDto     {@link QuoteRequestVendorDto}
     * @return                          {@link QuoteRequestVendor}
     */
    @Override
    protected QuoteRequestVendor convertToEntity(QuoteRequestVendorDto quoteRequestVendorDto) {
        return null;
    }

    @Override
    protected QuoteRequestVendor updateEntity(QuoteRequestVendorDto dto, QuoteRequestVendor entity) {
        return null;
    }

    /**
     * This method will prepare quoteRequestVendorDtoList for save items
     *
     * @param quoteRequestVendorDtoList {@link List<QuoteRequestVendorDto>}
     */
    protected List<QuoteRequestVendor> createOrUpdate(List<QuoteRequestVendorDto> quoteRequestVendorDtoList, QuoteRequest quoteRequest, Long rfqId) {
        Set<Long> quoteRequestVendorIds = quoteRequestVendorDtoList.stream().map(QuoteRequestVendorDto::getId).collect(Collectors.toSet());

        if (quoteRequestVendorDtoList.size() != quoteRequestVendorDtoList.stream().map(QuoteRequestVendorDto::getVendorId)
                .collect(Collectors.toSet()).size()) {
            throw EngineeringManagementServerException.badRequest(ErrorId.VENDOR_MUST_NOT_BE_DUPLICATE);
        }

        List<QuoteRequestVendor> quoteRequestVendorList = getAllByDomainIdIn(quoteRequestVendorIds, true);

        Map<Long, QuoteRequestVendor> quoteRequestVendorMap = quoteRequestVendorList.stream()
                .collect(Collectors.toMap(QuoteRequestVendor::getId, Function.identity()));

        if(quoteRequestVendorList.stream().anyMatch(quoteRequestVendor -> !Objects.equals(quoteRequestVendor.getQuoteRequestId(), rfqId))){
            throw EngineeringManagementServerException.badRequest(ErrorId.QUOTE_REQUEST_VENDOR_DOES_NOT_EXIST_UNDER_THE_QUOTE_REQUEST);
        }

        Set<Long> vendorIds = quoteRequestVendorDtoList.stream().map(QuoteRequestVendorDto::getVendorId).collect(Collectors.toSet());

        Map<Long, Vendor> vendorMap = vendorService.getAllByDomainIdIn(vendorIds, true).stream()
                .collect(Collectors.toMap(Vendor::getId, Function.identity()));

        return saveItemList(quoteRequestVendorDtoList.stream().map(quoteRequestVendorDto -> convertToEntity(quoteRequestVendorDto,
                quoteRequestVendorMap.getOrDefault(quoteRequestVendorDto.getId(), new QuoteRequestVendor()), quoteRequest,
                vendorMap.get(quoteRequestVendorDto.getVendorId()))).collect(Collectors.toList()));
    }

    private QuoteRequestVendor convertToEntity(QuoteRequestVendorDto quoteRequestVendorDto,
                                               QuoteRequestVendor quoteRequestVendor,
                                               QuoteRequest quoteRequest, Vendor vendor) {
        quoteRequestVendor.setRequestDate(quoteRequestVendorDto.getRequestDate());
        quoteRequestVendor.setVendor(vendor);
        quoteRequestVendor.setQuoteRequest(quoteRequest);
        return quoteRequestVendor;
    }

    public RfqVendorResponseDto getAllQuoteRequestVendorByRfq(QuoteRequestViewModel viewModel) {

        List<QuoteRequestVendor> quoteRequestVendorList = Objects.nonNull(viewModel.getId()) ?
                quoteRequestVendorRepository.findByQuoteRequestId(viewModel.getId()) : new ArrayList<>();

        return prepareQuoteRequestVendorViewModel(quoteRequestVendorList, viewModel);
    }

    /**
     * This method will prepare Quote Request VendorRequest View model list
     *
     * @param quoteRequestViewModel  {@link QuoteRequestViewModel}
     * @return {@link List< RfqRequestDto >}
     */
    protected RfqVendorResponseDto prepareQuoteRequestVendorViewModel(List<QuoteRequestVendor> quoteRequestVendorList,
                                                                      QuoteRequestViewModel quoteRequestViewModel) {
        RfqVendorResponseDto rfqVendorResponseDto = new RfqVendorResponseDto();
        rfqVendorResponseDto.setRequisitionId(quoteRequestViewModel.getRequisitionId());
        rfqVendorResponseDto.setRfqNo(quoteRequestViewModel.getRfqNo());
        rfqVendorResponseDto.setVoucherNo(quoteRequestViewModel.getVoucherNo());
        rfqVendorResponseDto.setId(quoteRequestViewModel.getId());
        rfqVendorResponseDto.setPartOrderId(quoteRequestViewModel.getPartOrderId());
        rfqVendorResponseDto.setOrderNo(quoteRequestViewModel.getOrderNo());
        rfqVendorResponseDto.setRfqType(quoteRequestViewModel.getRfqType());
        rfqVendorResponseDto.setInputType(quoteRequestViewModel.getInputType());
        rfqVendorResponseDto.setRejectedDesc(quoteRequestViewModel.getRejectedDesc());
        rfqVendorResponseDto.setIsRejected(quoteRequestViewModel.getIsRejected());
        rfqVendorResponseDto.setQuoteRequestVendorModelList(convertToQuoteRequestVendorViewModel(quoteRequestVendorList));
        return rfqVendorResponseDto;
    }

    @Override
    public PageData search(QuoteRequestVendorSearchDto searchDto, Pageable pageable) {
        Specification<QuoteRequestVendor> propellerSpecification = buildSpecification(searchDto)
                .and(new CustomSpecification<QuoteRequestVendor>()
                        .active(!Objects.nonNull(
                                searchDto.getIsActive()) || searchDto.getIsActive(), IS_ACTIVE_FIELD));
        Page<QuoteRequestVendor> pagedData = quoteRequestVendorRepository.findAll(propellerSpecification, pageable);
        List<QrVendorViewModel> models = convertToQuoteRequestVendorViewModel(pagedData.getContent());
        return PageData.builder()
                .model(models)
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    @Override
    protected Specification<QuoteRequestVendor> buildSpecification(QuoteRequestVendorSearchDto searchDto) {
        CustomSpecification<QuoteRequestVendor> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification
                .equalSpecificationAtRoot(searchDto.getVendorType(), ApplicationConstant.VENDOR_TYPE)
                .and(customSpecification.equalSpecificationAtRoot(searchDto.getQuoteRequestId(),
                        ApplicationConstant.QUOTE_REQUEST_ID)));
    }

    /**
     * This method will set vendor name on QuoteRequestVendorViewModel
     * @param quoteRequestVendorList       {@link List<QuoteRequestVendor>}
     */
    private List<QrVendorViewModel> convertToQuoteRequestVendorViewModel(
            List<QuoteRequestVendor> quoteRequestVendorList) {
        List<QrVendorViewModel> qrVendorViewModelList = new ArrayList<>();

        for (QuoteRequestVendor quoteRequestVendor : quoteRequestVendorList) {
            QrVendorViewModel qrVendorViewModel = new QrVendorViewModel();
            qrVendorViewModel.setId(quoteRequestVendor.getId());
            qrVendorViewModel.setQuoteRequestId(quoteRequestVendor.getQuoteRequestId());
            qrVendorViewModel.setRequestDate(quoteRequestVendor.getRequestDate());
            if(Objects.nonNull(quoteRequestVendor.getVendor())){
                qrVendorViewModel.setVendorId(quoteRequestVendor.getVendor().getId());
                qrVendorViewModel.setVendorName(quoteRequestVendor.getVendor().getName());
                qrVendorViewModel.setVendorType(quoteRequestVendor.getVendor().getVendorType());
                qrVendorViewModel.setVendorWorkFlowName(quoteRequestVendor.getVendor().getWorkFlowAction().getName());
            }
            qrVendorViewModelList.add(qrVendorViewModel);
        }
        return qrVendorViewModelList;
    }
}
