package com.digigate.engineeringmanagement.storeinspector.service.storeinspector;

import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.storeinspector.entity.storeinspector.StoreInspectionGrn;
import com.digigate.engineeringmanagement.storeinspector.payload.request.storeinspector.StoreInspectionGrnRequestDto;
import com.digigate.engineeringmanagement.storeinspector.payload.response.storeinspector.StoreInspectionGrnResponseDto;
import com.digigate.engineeringmanagement.storeinspector.repository.storeinspector.StoreInspectionGrnRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;

@Service
public class StoreInspectionGrnService extends AbstractSearchService<StoreInspectionGrn, StoreInspectionGrnRequestDto, IdQuerySearchDto> {

    private final StoreInspectionGrnRepository storeInspectionGrnRepository;

    public StoreInspectionGrnService(StoreInspectionGrnRepository storeInspectionGrnRepository) {
        super(storeInspectionGrnRepository);
        this.storeInspectionGrnRepository = storeInspectionGrnRepository;
    }

    @Override
    public StoreInspectionGrn create(StoreInspectionGrnRequestDto storeInspectionGrnRequestDto) {
        validateClientData(storeInspectionGrnRequestDto, null);
        StoreInspectionGrn entity = convertToEntity(storeInspectionGrnRequestDto);
        return saveItem(entity);
    }

    @Override
    public StoreInspectionGrn update(StoreInspectionGrnRequestDto storeInspectionGrnRequestDto, Long id) {
        validateClientData(storeInspectionGrnRequestDto, id);
        final StoreInspectionGrn entity = updateEntity(storeInspectionGrnRequestDto, findByIdUnfiltered(id));
        return saveItem(entity);
    }

    @Override
    public StoreInspectionGrnResponseDto getSingle(Long id) {
        StoreInspectionGrn storeInspectionGrn = findByIdUnfiltered(id);
        return convertToResponseDto(storeInspectionGrn);
    }

    @Override
    protected StoreInspectionGrnResponseDto convertToResponseDto(StoreInspectionGrn storeInspectionGrn) {
        StoreInspectionGrnResponseDto storeInspectionGrnResponseDto = new StoreInspectionGrnResponseDto();
        storeInspectionGrnResponseDto.setId(storeInspectionGrn.getId());
        storeInspectionGrnResponseDto.setCreatedDate(storeInspectionGrn.getCreatedDate());
        storeInspectionGrnResponseDto.setGrnNo(storeInspectionGrn.getGrnNo());
        storeInspectionGrnResponseDto.setUsed(storeInspectionGrn.getIsUsed());
        return storeInspectionGrnResponseDto;
    }

    @Override
    public StoreInspectionGrn convertToEntity(StoreInspectionGrnRequestDto storeInspectionGrnRequestDto) {
        StoreInspectionGrn storeInspectionGrn = new StoreInspectionGrn();
        storeInspectionGrn.setCreatedDate(storeInspectionGrnRequestDto.getCreatedDate());
        storeInspectionGrn.setGrnNo(storeInspectionGrnRequestDto.getGrnNo());
        return storeInspectionGrn;
    }

    @Override
    public StoreInspectionGrn updateEntity(StoreInspectionGrnRequestDto storeInspectionGrnRequestDto, StoreInspectionGrn entity) {
        entity.setCreatedDate(storeInspectionGrnRequestDto.getCreatedDate());
        entity.setGrnNo(storeInspectionGrnRequestDto.getGrnNo());
        return entity;
    }

    @Override
    protected Specification<StoreInspectionGrn> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<StoreInspectionGrn> customSpecification = new CustomSpecification<>();
        return Specification.where(
                customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), GRN_NO)
                       .and(customSpecification.active(searchDto.getIsUsed(), IS_USED)));
    }

    public void setIsUsedGrnNo(Long id) {
        final StoreInspectionGrn storeInspectionGrn = findByIdUnfiltered(id);
        storeInspectionGrn.setIsUsed(Boolean.TRUE);
        storeInspectionGrnRepository.save(storeInspectionGrn);
    }
}
