package com.digigate.engineeringmanagement.logistic.service;

import com.digigate.engineeringmanagement.logistic.entity.DutyFee;
import com.digigate.engineeringmanagement.logistic.entity.DutyFeeItem;
import com.digigate.engineeringmanagement.logistic.payload.request.DutyFeeItemRequestDto;
import com.digigate.engineeringmanagement.logistic.payload.response.DutyFeeItemResponseDto;
import com.digigate.engineeringmanagement.logistic.repository.DutyFeeItemRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.CurrencyService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DutyFeeItemService  {
    private final CurrencyService currencyService;
    private final DutyFeeItemRepository repository;
    public DutyFeeItemService(CurrencyService currencyService, DutyFeeItemRepository repository) {
        this.currencyService = currencyService;
        this.repository=repository;
    }


    public List<DutyFeeItem> saveAll(List<DutyFeeItemRequestDto> dutyFeeItemRequestDtoList, DutyFee dutyFee) {
        List<DutyFeeItem> dutyFeeItems = new ArrayList<>();
        dutyFeeItemRequestDtoList.forEach(dutyFeeItemRequestDto -> prepareEntity(dutyFeeItemRequestDto, new DutyFeeItem(), dutyFee, dutyFeeItems));
        return repository.saveAll(dutyFeeItems);
    }

    public List<DutyFeeItem> updateAll(List<DutyFeeItemRequestDto> dutyFeeItemRequestDtoList, DutyFee dutyFee) {
        Map<Long, DutyFeeItem> dutyFeeItemMap = repository.findByDutyFeeId(dutyFee.getId())
                .stream().collect(Collectors.toMap(DutyFeeItem::getId, Function.identity()));
        List<DutyFeeItem> dutyFeeItems = new ArrayList<>();
        dutyFeeItemRequestDtoList.forEach(dutyFeeItemRequestDto -> prepareUpdateEntity(dutyFeeItemRequestDto, dutyFeeItemMap,
                dutyFee, dutyFeeItems));
        return repository.saveAll(dutyFeeItems);

    }

    private void prepareUpdateEntity(DutyFeeItemRequestDto dutyFeeItemRequestDto, Map<Long, DutyFeeItem> dutyFeeItemMap,
                                     DutyFee dutyFee, List<DutyFeeItem> dutyFeeItems) {
        dutyFeeItems.add(convertToEntity(dutyFeeItemRequestDto,dutyFeeItemMap.getOrDefault(dutyFeeItemRequestDto.getId(),new DutyFeeItem()),dutyFee));
    }

    private void prepareEntity(DutyFeeItemRequestDto dutyFeeItemRequestDto, DutyFeeItem dutyFeeItem, DutyFee dutyFee, List<DutyFeeItem> dutyFeeItems) {
        dutyFeeItems.add(convertToEntity(dutyFeeItemRequestDto, dutyFeeItem, dutyFee));
    }

    private DutyFeeItem convertToEntity(DutyFeeItemRequestDto dutyFeeItemRequestDto, DutyFeeItem dutyFeeItem, DutyFee dutyFee) {
        dutyFeeItem.setFees(dutyFeeItemRequestDto.getFees());
        dutyFeeItem.setTotalAmount(dutyFeeItemRequestDto.getTotalAmount());
        dutyFeeItem.setDutyFee(dutyFee);
        if(Objects.nonNull(dutyFeeItemRequestDto.getIsActive())){
            dutyFeeItem.setIsActive(dutyFeeItemRequestDto.getIsActive());
        }
        if(Objects.nonNull(dutyFeeItemRequestDto.getCurrencyId())){
            dutyFeeItem.setCurrency(currencyService.findById(dutyFeeItemRequestDto.getCurrencyId()));
        }
        return dutyFeeItem;
    }

    private DutyFeeItemResponseDto convertToResponseDto(DutyFeeItem dutyFeeItem){
        DutyFeeItemResponseDto responseDto = new DutyFeeItemResponseDto();
        responseDto.setId(dutyFeeItem.getId());
        responseDto.setFees(dutyFeeItem.getFees());
        responseDto.setTotalAmount(dutyFeeItem.getTotalAmount());
        responseDto.setCurrencyId(dutyFeeItem.getCurrencyId());
        responseDto.setCurrencyCode(dutyFeeItem.getCurrency().getCode());
        responseDto.setIsActive(dutyFeeItem.getIsActive());
        return responseDto;
    }

    public List<DutyFeeItemResponseDto> getResponseData(Long parentId){
        List<DutyFeeItem> dutyFeeItemList = repository.findByDutyFeeIdAndIsActiveTrue(parentId);
        return dutyFeeItemList.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }

}
