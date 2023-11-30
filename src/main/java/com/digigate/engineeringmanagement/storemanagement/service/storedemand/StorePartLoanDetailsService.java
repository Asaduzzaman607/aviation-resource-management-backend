package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreDemandItem;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StorePartLoan;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StorePartLoanDetails;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartLoanDetailDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.StorePartLoanDetailsRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StorePartLoanDetailsService extends AbstractSearchService<StorePartLoanDetails, StorePartLoanDetailDto, IdQuerySearchDto> {
    private final StorePartLoanDetailsRepository storePartLoanDetailsRepository;
    private final PartRemarkService partRemarkService;

    public StorePartLoanDetailsService(StorePartLoanDetailsRepository storePartLoanDetailsRepository, PartRemarkService partRemarkService) {
        super(storePartLoanDetailsRepository);
        this.storePartLoanDetailsRepository = storePartLoanDetailsRepository;
        this.partRemarkService = partRemarkService;
    }

    /**
     * Custom save all
     *
     * @param storePartLoanDetailDtoList {@link StorePartLoanDetailDto}
     * @param storePartLoan              {@link StorePartLoan}
     */
    public void saveAll(List<StorePartLoanDetailDto> storePartLoanDetailDtoList, StorePartLoan storePartLoan) {
        List<StorePartLoanDetails> storePartLoanDetails = storePartLoanDetailDtoList.stream()
                .map(storePartLoanDetailDto -> convertToSaveEntity(storePartLoanDetailDto, storePartLoan))
                .collect(Collectors.toList());
        List<StorePartLoanDetails> storeReturnPartList = super.saveItemList(storePartLoanDetails);
        saveOrUpdateWithRemarks(storeReturnPartList, storePartLoan.getId());
    }

    /**
     * Custom update
     *
     * @param storePartLoanDetailDtoList {@link StorePartLoanDetailDto}
     * @param storePartLoan              {@link StorePartLoan}
     */
    public void updateAll(List<StorePartLoanDetailDto> storePartLoanDetailDtoList, StorePartLoan storePartLoan) {
        Set<Long> detailsSet = storePartLoanDetailDtoList.stream().map(StorePartLoanDetailDto::getId)
                .filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, StorePartLoanDetails> storePartLoanDetailsMap = getAllByDomainIdInUnfiltered(detailsSet).stream()
                .collect(Collectors.toMap(StorePartLoanDetails::getId, Function.identity()));

        List<StorePartLoanDetails> storePartLoanDetails = storePartLoanDetailDtoList.stream().map(storePartLoanDetailDto ->
                        convertToUpdateEntity(storePartLoanDetailDto, storePartLoanDetailsMap.getOrDefault(storePartLoanDetailDto.getId(),
                                new StorePartLoanDetails()), storePartLoan))
                .collect(Collectors.toList());

        List<StorePartLoanDetails> storeReturnPartList = super.saveItemList(storePartLoanDetails);
        saveOrUpdateWithRemarks(storeReturnPartList, storePartLoan.getId());
    }
    public List<StorePartLoanDetails> findByStorePartLoanIdIn(Set<Long> loanIds){
        return storePartLoanDetailsRepository.findByStorePartLoanIdInAndIsActiveTrue(loanIds);
    }

    public List<StorePartLoanDetailDto> convertToResponse(List<StorePartLoanDetails> storePartLoanDetails){
        return storePartLoanDetails.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    @Override
    protected Specification<StorePartLoanDetails> buildSpecification(IdQuerySearchDto searchDto) {
        return null;
    }

    @Override
    protected StorePartLoanDetailDto convertToResponseDto(StorePartLoanDetails storePartLoanDetails) {
        return StorePartLoanDetailDto.builder()
                .id(storePartLoanDetails.getId())
                .serialId(storePartLoanDetails.getStorePartSerialId())
                .storePartLoanId(storePartLoanDetails.getStorePartLoanId())
                .isActive(storePartLoanDetails.getIsActive())
                .build();
    }

    @Override
    protected StorePartLoanDetails convertToEntity(StorePartLoanDetailDto storePartLoanDetailDto) {
        return null;
    }

    @Override
    protected StorePartLoanDetails updateEntity(StorePartLoanDetailDto dto, StorePartLoanDetails entity) {
        return null;
    }

    /**
     * Convert to entity for update
     *
     * @param dto                  {@link StorePartLoanDetailDto}
     * @param storePartLoanDetails {@link StorePartLoanDetails}
     * @param storePartLoan        {@link StorePartLoan}
     * @return Successfully updated message
     */
    private StorePartLoanDetails convertToUpdateEntity(StorePartLoanDetailDto dto,
                                              StorePartLoanDetails storePartLoanDetails,
                                              StorePartLoan storePartLoan) {
        storePartLoanDetails.setIsActive(dto.getIsActive());
        storePartLoanDetails.setStorePartLoan(storePartLoan);
        storePartLoanDetails.setStorePartSerialId(dto.getSerialId());
        return storePartLoanDetails;
    }

    /**
     * Convert to entity for save
     *
     * @param dto           {@link StorePartLoanDetailDto}
     * @param storePartLoan {@link StorePartLoan}
     * @return Successfully created message
     */
    private StorePartLoanDetails convertToSaveEntity(StorePartLoanDetailDto dto, StorePartLoan storePartLoan) {
        StorePartLoanDetails storePartLoanDetails = new StorePartLoanDetails();
        storePartLoanDetails.setIsActive(dto.getIsActive());
        storePartLoanDetails.setStorePartLoan(storePartLoan);
        storePartLoanDetails.setStorePartSerialId(dto.getSerialId());
        storePartLoanDetails.setRemarks(dto.getRemarks());
        return storePartLoanDetails;
    }

    private void saveOrUpdateWithRemarks(List<StorePartLoanDetails> storePartLoanDetailsList, Long parentId) {
        Map<Long, String> storePartLoanDetailsMap = storePartLoanDetailsList.stream()
                .collect(Collectors.toMap(StorePartLoanDetails::getId, StorePartLoanDetails::getRemarks));
        partRemarkService.saveOrUpdateRemarks(storePartLoanDetailsMap, parentId, RemarkType.STORE_PART_LOAN);
    }
}
