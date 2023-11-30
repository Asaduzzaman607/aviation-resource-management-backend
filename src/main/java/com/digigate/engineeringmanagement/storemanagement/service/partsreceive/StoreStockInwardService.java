package com.digigate.engineeringmanagement.storemanagement.service.partsreceive;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.entity.User;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.service.UserService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.IqItemProjection;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartsInvoice;
import com.digigate.engineeringmanagement.procurementmanagement.service.PartsInvoicesService;
import com.digigate.engineeringmanagement.procurementmanagement.util.CsPartUtilService;
import com.digigate.engineeringmanagement.storeinspector.payload.projection.InwardPartOrderProjection;
import com.digigate.engineeringmanagement.storemanagement.constant.FeatureName;
import com.digigate.engineeringmanagement.storemanagement.entity.partsreceive.StoreStockInward;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreStockInwardProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.UsernameProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.partsreceive.StoreStockInwardDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.PartSerialsViewModel;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.StoreStockInwardViewModel;
import com.digigate.engineeringmanagement.storemanagement.repository.partsreceive.StoreStockInwardRepository;
import com.digigate.engineeringmanagement.storemanagement.service.StoreVoucherTrackingService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.GenericAttachmentService;
import com.digigate.engineeringmanagement.storemanagement.util.SortChanger;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;

@Service
public class StoreStockInwardService extends AbstractSearchService<StoreStockInward, StoreStockInwardDto, IdQuerySearchDto> {

    private final StoreStockInwardRepository storeStockInwardRepository;
    private final UserService userService;
    private final StoreVoucherTrackingService voucherTrackingService;
    private final GenericAttachmentService genericAttachmentService;
    private final PartsInvoicesService partsInvoicesService;
    private final CsPartUtilService csPartUtilService;

    /**
     * Constructor parameterized
     *
     * @param repository        {@link StoreStockInwardRepository}
     * @param userService       {@link UserService}
     */
    public StoreStockInwardService(StoreStockInwardRepository repository,
                                   UserService userService,
                                   StoreVoucherTrackingService voucherTrackingService,
                                   GenericAttachmentService genericAttachmentService,
                                   PartsInvoicesService partsInvoicesService,
                                   CsPartUtilService csPartUtilService) {
        super(repository);
        this.storeStockInwardRepository = repository;
        this.userService = userService;
        this.voucherTrackingService = voucherTrackingService;
        this.genericAttachmentService = genericAttachmentService;
        this.partsInvoicesService = partsInvoicesService;
        this.csPartUtilService = csPartUtilService;
    }

    public List<PartSerialsViewModel> getPartsFromPartOrder(Long inwardId) {
        List<IqItemProjection> iqItemProjections = storeStockInwardRepository.getPartsFromPartOrder(inwardId);
        return populateToPartSerials(iqItemProjections);
    }

    public StoreStockInwardProjection findStockInwardById(Long stockInwardId) {
        return storeStockInwardRepository.findStoreStockInwardById(stockInwardId);
    }

    @Override
    public StoreStockInward create(StoreStockInwardDto storeStockInwardDto) {
        StoreStockInward storeStockInward = super.create(storeStockInwardDto);
        if (CollectionUtils.isNotEmpty(storeStockInwardDto.getAttachments())) {
            genericAttachmentService.saveAllAttachments(storeStockInwardDto.getAttachments(), FeatureName.STOCK_INWARD,
                storeStockInward.getId());
        }
        return storeStockInward;
    }

    @Override
    public StoreStockInward update(StoreStockInwardDto storeStockInwardDto, Long id) {
        if (CollectionUtils.isNotEmpty(storeStockInwardDto.getAttachments())) {
            genericAttachmentService.updateByRecordId(FeatureName.STOCK_INWARD, id, storeStockInwardDto.getAttachments());
        }
        return super.update(storeStockInwardDto, id);
    }

    /**
     * This method is responsible for getting serial no from parent
     *
     * @param idList {@link StoreStockInward}
     * @return responding projection {@link StoreStockInwardProjection}
     */
    public List<StoreStockInwardProjection> findSerialNoByIdList(Set<Long> idList) {
        return storeStockInwardRepository.findByIdIn(idList);
    }

    /**
     * This method is responsible for getting serial no from parent
     *
     * @param id {@link StoreStockInward}
     * @return responding projection {@link StoreStockInwardProjection}
     */
    public StoreStockInwardProjection findSerialNoById(Long id) {
        return storeStockInwardRepository.findStoreStockInwardById(id);
    }

    @Override
    public StoreStockInwardViewModel getSingle(Long id) {
        StoreStockInwardViewModel storeStockInwardViewModel = getListOfStockInwardResponse(Collections.singletonList(findByIdUnfiltered(id))).stream().findFirst()
            .orElseThrow(() -> EngineeringManagementServerException.notFound(ErrorId.DATA_NOT_FOUND));
        storeStockInwardViewModel.setAttachments(genericAttachmentService.getLinksByFeatureNameAndId(FeatureName.STOCK_INWARD, id));
        return storeStockInwardViewModel;
    }

    @Override
    public PageData getAll(Boolean isActive, Pageable pageable) {
        Page<StoreStockInward> externalDepartmentPage = storeStockInwardRepository.findAllByIsActive(isActive, pageable);
        return getResponseData(externalDepartmentPage, pageable);
    }

    @Override
    public PageData search(IdQuerySearchDto searchDto, Pageable pageable) {
        pageable = SortChanger.descendingSortByCreatedAt(pageable);
        Specification<StoreStockInward> storeStockInwardSpecification = buildSpecification(searchDto);
        Page<StoreStockInward> pagedData = storeStockInwardRepository.findAll(storeStockInwardSpecification, pageable);
        return getResponseData(pagedData, pageable);
    }

    /**
     * This method is responsible for converting dto to entity
     *
     * @param storeStockInwardDto {@link StoreStockInwardDto}
     * @return responding entity {@link StoreStockInward}
     */
    @Override
    protected StoreStockInward convertToEntity(StoreStockInwardDto storeStockInwardDto) {
        return populateEntity(storeStockInwardDto, new StoreStockInward());
    }

    /**
     * This method is responsible for updating entity from dto
     *
     * @param dto    {@link StoreStockInwardDto}
     * @param entity {@link StoreStockInward}
     * @return responding {@link StoreStockInward}
     */
    @Override
    protected StoreStockInward updateEntity(StoreStockInwardDto dto, StoreStockInward entity) {
        return populateEntity(dto, entity);
    }

    @Override
    protected <T> T convertToResponseDto(StoreStockInward storeStockInward) {
        return null;
    }

    /**
     * This method is responsible for building search specification
     *
     * @param searchDto {@link IdQuerySearchDto}
     * @return responding entity {@link StoreStockInward}
     */
    @Override
    protected Specification<StoreStockInward> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<StoreStockInward> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.equalSpecificationAtChild(searchDto.getId(), PART_INVOICE, ID).
                and(customSpecification.active(searchDto.getIsActive(), IS_ACTIVE_FIELD)).
                and(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), VOUCHER_NO)));
    }

    /**
     * This method is responsible for copying attribute dto to entity
     *
     * @param storeStockInwardDto {@link StoreStockInwardDto}
     * @param storeStockInward    {@link StoreStockInward}
     * @return responding entity {@link StoreStockInward}
     */
    private StoreStockInward populateEntity(StoreStockInwardDto storeStockInwardDto, StoreStockInward storeStockInward) {
        if (Objects.isNull(storeStockInward.getId())) {
            PartsInvoice partsInvoice = partsInvoicesService.findByInvoiceNo(storeStockInwardDto.getInvoiceNo());
            storeStockInward.setVoucherNo(voucherTrackingService.generateUniqueVoucherNo(partsInvoice.getId(),
                    VoucherType.SIB, partsInvoice.getInvoiceNo()));
            storeStockInward.setPartsInvoice(partsInvoice);
            storeStockInward.setReceivedBy(Objects.isNull(storeStockInward.getReceivedBy()) ? User.withId(Helper.getAuthUserId()) :
                    User.withId(storeStockInwardDto.getReceivedBy()));
        } else {
            Long receivedBy = storeStockInwardDto.getReceivedBy();
            if (Objects.nonNull(receivedBy)) {
                storeStockInward.setReceivedBy(User.withId(receivedBy));
            }
        }
        storeStockInward.setReceiveDate(storeStockInwardDto.getReceiveDate());
        storeStockInward.setTptMode(storeStockInwardDto.getTptMode());
        storeStockInward.setFlightNo(storeStockInwardDto.getFlightNo());
        storeStockInward.setArrivalDate(storeStockInwardDto.getArrivalDate());
        storeStockInward.setAirwaysBill(storeStockInwardDto.getAirwaysBill());
        storeStockInward.setPackingMode(storeStockInwardDto.getPackingMode());
        storeStockInward.setPackingNo(storeStockInwardDto.getPackingNo());
        storeStockInward.setWeight(storeStockInwardDto.getWeight());
        storeStockInward.setNoOfItems(storeStockInwardDto.getNoOfItems());
        storeStockInward.setDescription(storeStockInwardDto.getDescription());
        storeStockInward.setImportNo(storeStockInwardDto.getImportNo());
        storeStockInward.setImportDate(storeStockInwardDto.getImportDate());
        storeStockInward.setDiscrepancyReportNo(storeStockInwardDto.getDiscrepancyReportNo());
        storeStockInward.setRemarks(storeStockInwardDto.getRemarks());
        return storeStockInward;
    }

    /**
     * This method is responsible for converting response entity to view model
     *
     * @param storeStockInward {@link StoreStockInward}
     * @return responding view model {@link StoreStockInwardViewModel}
     */
    private StoreStockInwardViewModel convertToViewModel(StoreStockInward storeStockInward, UsernameProjection usernameProjection,
                                                         InwardPartOrderProjection inwardPartOrderProjection) {

        StoreStockInwardViewModel storeStockInwardViewModel = new StoreStockInwardViewModel();

        storeStockInwardViewModel.setId(storeStockInward.getId());
        storeStockInwardViewModel.setSerialNo(storeStockInward.getVoucherNo());
        storeStockInwardViewModel.setReceiveDate(storeStockInward.getReceiveDate());
        storeStockInwardViewModel.setTptMode(storeStockInward.getTptMode());
        storeStockInwardViewModel.setFlightNo(storeStockInward.getFlightNo());
        storeStockInwardViewModel.setArrivalDate(storeStockInward.getArrivalDate());
        storeStockInwardViewModel.setAirwaysBill(storeStockInward.getAirwaysBill());
        if (Objects.nonNull(storeStockInward.getPartsInvoice())) {
            storeStockInwardViewModel.setInvoiceNo(storeStockInward.getPartsInvoice().getInvoiceNo());
        }
        storeStockInwardViewModel.setPackingMode(storeStockInward.getPackingMode());
        storeStockInwardViewModel.setPackingNo(storeStockInward.getPackingNo());
        storeStockInwardViewModel.setWeight(storeStockInward.getWeight());
        storeStockInwardViewModel.setNoOfItems(storeStockInward.getNoOfItems());
        storeStockInwardViewModel.setDescription(storeStockInward.getDescription());
        storeStockInwardViewModel.setImportNo(storeStockInward.getImportNo());
        storeStockInwardViewModel.setImportDate(storeStockInward.getImportDate());
        storeStockInwardViewModel.setDiscrepancyReportNo(storeStockInward.getDiscrepancyReportNo());
        storeStockInwardViewModel.setRemarks(storeStockInward.getRemarks());
        if (Objects.nonNull(usernameProjection)) {
            storeStockInwardViewModel.setReceivedBy(usernameProjection.getId());
            storeStockInwardViewModel.setReceiverName(usernameProjection.getLogin());
        }
        if (Objects.nonNull(inwardPartOrderProjection)) {
            storeStockInwardViewModel.setOrderId(inwardPartOrderProjection.getId());
            storeStockInwardViewModel.setOrderNo(inwardPartOrderProjection.getOrderNo());
        }
        return storeStockInwardViewModel;
    }

    /**
     * This method is responsible for getting and mapping all data in one database call
     *
     * @param storeStockInwardList {@link StoreStockInward}
     * @return responding list of store stock inward response
     */
    private List<StoreStockInwardViewModel> getListOfStockInwardResponse(List<StoreStockInward> storeStockInwardList) {
        Set<Long> userIdList = storeStockInwardList.stream().map(StoreStockInward::getReceivedById).collect(Collectors.toSet());
        Map<Long, UsernameProjection> usernameProjectionMap = userService.findUsernameByIdList(userIdList).stream()
            .collect(Collectors.toMap(UsernameProjection::getId, Function.identity()));

        Set<Long> inwardIdSet = storeStockInwardList.stream().map(StoreStockInward::getId).collect(Collectors.toSet());
        Map<Long, InwardPartOrderProjection> partOrderProjectionMap = storeStockInwardRepository.getPoFromStockInwardIdIn(inwardIdSet)
                .stream().collect(Collectors.toMap(InwardPartOrderProjection::getInwardId, Function.identity()));

        return storeStockInwardList.stream().map(storeStockInward -> convertToViewModel(storeStockInward,
                        usernameProjectionMap.get(storeStockInward.getReceivedById()),
                        partOrderProjectionMap.get(storeStockInward.getId())))
                .collect(Collectors.toList());
    }

    private PageData getResponseData(Page<StoreStockInward> pagedData, Pageable pageable) {
        return PageData.builder().model(getListOfStockInwardResponse(pagedData.getContent())).totalPages(pagedData.getTotalPages()).totalElements(pagedData.getTotalElements()).currentPage(pageable.getPageNumber() + 1).build();
    }

    private List<PartSerialsViewModel> populateToPartSerials(List<IqItemProjection> iqItemProjections) {
        return iqItemProjections.stream().map(this::populateToDetails).collect(Collectors.toList());
    }

    private PartSerialsViewModel populateToDetails(IqItemProjection iqItemProjection) {
        PartSerialsViewModel partSerialsViewModel = new PartSerialsViewModel();
        partSerialsViewModel.setPartId(csPartUtilService.getPartId(iqItemProjection));
        partSerialsViewModel.setPartNo(csPartUtilService.getPartNo(iqItemProjection));
        partSerialsViewModel.setPartDescription(csPartUtilService.getPartDescription(iqItemProjection));
        partSerialsViewModel.setVendorSerials(iqItemProjection.getVendorSerials());

        return partSerialsViewModel;
    }
}
