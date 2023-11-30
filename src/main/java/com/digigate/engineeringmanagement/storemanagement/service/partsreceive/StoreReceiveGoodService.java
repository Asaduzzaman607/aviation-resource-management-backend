package com.digigate.engineeringmanagement.storemanagement.service.partsreceive;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.storemanagement.entity.partsreceive.StoreReceivedGood;
import com.digigate.engineeringmanagement.storemanagement.entity.partsreceive.StoreStockInward;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.RequisitionProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreStockInwardProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.partsreceive.StoreReceiveGoodDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.StoreReceiveGoodSearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.StoreReceiveGoodViewModel;
import com.digigate.engineeringmanagement.storemanagement.repository.partsreceive.StoreReceivedGoodRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ProcurementRequisitionService;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ProcurementRequisition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StoreReceiveGoodService extends AbstractSearchService<
        StoreReceivedGood,
        StoreReceiveGoodDto,
        StoreReceiveGoodSearchDto> {
    private final StoreStockInwardService storeStockInwardService;
    private final StoreReceivedGoodRepository storeReceivedGoodRepository;
    private final ProcurementRequisitionService requisitionService;

    /**
     * Constructor Parameterized
     *
     * @param receivedGoodRepository {@link StoreReceivedGoodRepository}
     * @param storeStockInwardService {@link StoreStockInwardService}
     * @param requisitionService {@link ProcurementRequisitionService}
     */
    public StoreReceiveGoodService(StoreReceivedGoodRepository receivedGoodRepository,
                                   StoreStockInwardService storeStockInwardService,
                                   ProcurementRequisitionService requisitionService) {
        super(receivedGoodRepository);
        this.storeStockInwardService = storeStockInwardService;
        this.storeReceivedGoodRepository = receivedGoodRepository;
        this.requisitionService = requisitionService;
    }

    /**
     * This method is responsible for checking requisition by id
     *
     * @param requisitionId {@link ProcurementRequisition}
     * @return responding primitive boolean
     */
    public boolean isPossibleInactiveRequisition(Long requisitionId){
        return storeReceivedGoodRepository.existsByRequisitionIdAndIsActiveTrue(requisitionId);
    }

    /**
     * This method is responsible for checking stock inward by id
     *
     * @param storeStockInwardId {@link StoreStockInward}
     * @return responding primitive boolean
     */
    public boolean isPossibleInactiveStockInward(Long storeStockInwardId){
        return storeReceivedGoodRepository
                .existsByStoreStockInwardIdAndIsActiveTrue(storeStockInwardId);
    }

    @Override
    public PageData search(StoreReceiveGoodSearchDto searchDto, Pageable pageable) {
        Specification<StoreReceivedGood> propellerSpecification = buildSpecification(searchDto)
                .and(new CustomSpecification<StoreReceivedGood>()
                        .active(searchDto.getIsActive(), ApplicationConstant.IS_ACTIVE_FIELD));
        Page<StoreReceivedGood> pagedData =
                storeReceivedGoodRepository.findAll(propellerSpecification, pageable);
        return PageData.builder()
                .model(getListOfReceiveGood(pagedData.getContent()))
                .totalPages(pagedData.getTotalPages())
                .totalElements(pagedData.getTotalElements())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    /**
     * This method is responsible converting dto to entity
     *
     * @param storeReceiveGoodDto {@link StoreReceiveGoodDto}
     * @return responding entity {@link StoreReceivedGood}
     */
    @Override
    protected StoreReceivedGood convertToEntity(StoreReceiveGoodDto storeReceiveGoodDto) {
        return populateDtoToEntity(storeReceiveGoodDto, new StoreReceivedGood());
    }


    /**
     * This method is responsible for converting entity to response
     *
     * @param storeReceivedGood {@link StoreReceivedGood}
     * @return responding view model {@link StoreReceiveGoodViewModel}
     */
    @Override
    protected StoreReceiveGoodViewModel convertToResponseDto(StoreReceivedGood storeReceivedGood) {
        StoreReceiveGoodViewModel storeReceiveGoodViewModel = new StoreReceiveGoodViewModel();
        storeReceiveGoodViewModel.setId(storeReceivedGood.getId());
        storeReceiveGoodViewModel.setGrDate(storeReceivedGood.getGrDate());

        if(Objects.nonNull(storeReceivedGood.getStoreStockInwardId())){
            StoreStockInwardProjection storeStockInwardProjection =
                    storeStockInwardService.findSerialNoById(storeReceivedGood.getStoreStockInwardId());
            if(Objects.nonNull(storeStockInwardProjection)){
                storeReceiveGoodViewModel.setStoreStockInwardId(storeReceivedGood.getStoreStockInwardId());
            }
        }
        if(Objects.nonNull(storeReceivedGood.getRequisitionId())){
            RequisitionProjection requisitionProjection =
                    requisitionService.findRequisitionById(storeReceivedGood.getRequisitionId());
            if(Objects.nonNull(requisitionProjection)){
                storeReceiveGoodViewModel.setRequisitionId(requisitionProjection.getId());
                storeReceiveGoodViewModel.setRequisitionNo(requisitionProjection.getVoucherNo());
            }
        }

        return storeReceiveGoodViewModel;
    }

    /**
     * This method is responsible for updating entity for dto
     *
     * @param dto {@link StoreReceiveGoodDto}
     * @param entity {@link StoreReceivedGood}
     * @return responding entity {@link StoreReceivedGood}
     */
    @Override
    protected StoreReceivedGood updateEntity(StoreReceiveGoodDto dto, StoreReceivedGood entity) {
        return populateDtoToEntity(dto, entity);
    }

    /**
     * This method is responsible for building specification for search
     *
     * @param storeReceiveGoodSearchDto {@link StoreReceiveGoodSearchDto}
     * @return responding specification {@link Specification}
     */
    @Override
    protected Specification<StoreReceivedGood> buildSpecification(
            StoreReceiveGoodSearchDto storeReceiveGoodSearchDto) {
        return new CustomSpecification<StoreReceivedGood>()
                .equalSpecificationAtRoot(storeReceiveGoodSearchDto.getGoodReceiveDate(), ApplicationConstant.GR_DATE);
    }

    /**
     * This method is responsible for populating create & update
     *
     * @param storeReceiveGoodDto {@link StoreReceiveGoodDto}
     * @param storeReceivedGood {@link StoreReceivedGood}
     * @return responding {@link StoreReceivedGood}
     */
    private StoreReceivedGood populateDtoToEntity(
            StoreReceiveGoodDto storeReceiveGoodDto, StoreReceivedGood storeReceivedGood){

        storeReceivedGood.setGrDate(storeReceiveGoodDto.getGrDate());
        if(!Objects.equals(
                storeReceiveGoodDto.getStoreStockInwardId(),
                storeReceivedGood.getStoreStockInwardId()
        )){
            storeReceivedGood.setStoreStockInward(
                    storeStockInwardService.findById(storeReceiveGoodDto.getStoreStockInwardId()));
        }
        if(!Objects.equals(
                storeReceiveGoodDto.getRequisitionId(),
                storeReceivedGood.getRequisitionId()
        )){
            storeReceivedGood.setProcurementRequisition(
                    requisitionService.findById(storeReceiveGoodDto.getRequisitionId()));
        }
        return storeReceivedGood;
    }

    /**
     * This method is responsible for converting entity to response
     *
     * @param storeReceivedGood {@link StoreReceivedGood}
     * @return responding view model {@link StoreReceiveGoodViewModel}
     */
    private StoreReceiveGoodViewModel convertToViewModel(
            StoreReceivedGood storeReceivedGood,
            StoreStockInwardProjection storeStockInwardProjection,
            RequisitionProjection requisitionProjection) {

        StoreReceiveGoodViewModel storeReceiveGoodViewModel = new StoreReceiveGoodViewModel();
        storeReceiveGoodViewModel.setId(storeReceivedGood.getId());
        storeReceiveGoodViewModel.setGrDate(storeReceivedGood.getGrDate());
        if(Objects.nonNull(storeStockInwardProjection)){
            storeReceiveGoodViewModel.setStoreStockInwardId(storeStockInwardProjection.getId());
        }
        if(Objects.nonNull(requisitionProjection)){
            storeReceiveGoodViewModel.setRequisitionId(requisitionProjection.getId());
            storeReceiveGoodViewModel.setRequisitionNo(requisitionProjection.getVoucherNo());
        }
        return storeReceiveGoodViewModel;
    }

    /**
     * This method is responsible for getting and mapping all data in one database call
     *
     * @param storeReceivedGoodList {@link StoreReceivedGood}
     * @return responding list of store receive good response
     */
    private List<StoreReceiveGoodViewModel> getListOfReceiveGood(List<StoreReceivedGood> storeReceivedGoodList){
        Set<Long> storeStockInwardIdList = storeReceivedGoodList
                .stream()
                .map(StoreReceivedGood::getStoreStockInwardId)
                .collect(Collectors.toSet());
        Map<Long, StoreStockInwardProjection> storeStockInwardProjectionMap =
                storeStockInwardService.findSerialNoByIdList(storeStockInwardIdList)
                        .stream()
                        .collect(Collectors.toMap(StoreStockInwardProjection::getId, Function.identity()));

        Set<Long> requisitionIdList = storeReceivedGoodList
                .stream()
                .map(StoreReceivedGood::getRequisitionId)
                .collect(Collectors.toSet());
        Map<Long, RequisitionProjection> requisitionProjectionMap =
                requisitionService.findRequisitionListByIdSet(requisitionIdList)
                        .stream()
                        .collect(Collectors.toMap(RequisitionProjection::getId, Function.identity()));

        return storeReceivedGoodList
                .stream()
                .map(storeReceivedGood ->
                        convertToViewModel(
                                storeReceivedGood,
                                storeStockInwardProjectionMap.get(storeReceivedGood.getStoreStockInwardId()),
                                requisitionProjectionMap.get(storeReceivedGood.getRequisitionId())))
                .collect(Collectors.toList());
    }
}
