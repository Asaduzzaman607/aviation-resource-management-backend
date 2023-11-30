package com.digigate.engineeringmanagement.storemanagement.service.planning;

import com.digigate.engineeringmanagement.planning.dto.AcBuildPartReturnDto;
import com.digigate.engineeringmanagement.planning.service.AircraftBuildIService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.ReturnPartsDetailDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreReturnPartRequestDto;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartInactiveInfoGetService {
    private final AircraftBuildIService aircraftBuildIService;

    public PartInactiveInfoGetService(AircraftBuildIService aircraftBuildIService) {
        this.aircraftBuildIService = aircraftBuildIService;
    }

    public void populateWithPartInfo(List<StoreReturnPartRequestDto> returnPartRequestDtoList) {
        returnPartRequestDtoList.forEach(this::populateToInfo);
    }

    private void populateToInfo(StoreReturnPartRequestDto storeReturnPartRequestDto) {
        Long removedPartId = storeReturnPartRequestDto.getPartId();
        Long planningSerialId = storeReturnPartRequestDto.getReturnPartsDetailDto().getRemovedPlanningSerialId();

        AcBuildPartReturnDto acBuildPartReturnDto = getPartInactiveInfo(removedPartId, planningSerialId);
        storeReturnPartRequestDto.setIsInactive(BooleanUtils.isTrue(acBuildPartReturnDto.getIsInactive()));
        populateToPartInactiveInfo(storeReturnPartRequestDto.getReturnPartsDetailDto(), acBuildPartReturnDto);
    }

    private void populateToPartInactiveInfo(ReturnPartsDetailDto returnPartsDetailDto,
                                            AcBuildPartReturnDto acBuildPartReturnDto) {
        returnPartsDetailDto.setPositionId(acBuildPartReturnDto.getPositionId());
        returnPartsDetailDto.setAircraftId(acBuildPartReturnDto.getAircraftId());
        returnPartsDetailDto.setReasonRemoved(acBuildPartReturnDto.getRemovalReason());
        returnPartsDetailDto.setRemovalDate(acBuildPartReturnDto.getRemovalDate());
        returnPartsDetailDto.setTsn(acBuildPartReturnDto.getTsn());
        returnPartsDetailDto.setCsn(acBuildPartReturnDto.getCsn());
        returnPartsDetailDto.setTso(acBuildPartReturnDto.getTso());
        returnPartsDetailDto.setCso(acBuildPartReturnDto.getCso());
        returnPartsDetailDto.setTsr(acBuildPartReturnDto.getTsr());
        returnPartsDetailDto.setCsr(acBuildPartReturnDto.getCsr());
        returnPartsDetailDto.setSign(acBuildPartReturnDto.getSign());
        returnPartsDetailDto.setAuthNo(acBuildPartReturnDto.getAuthNo());
        returnPartsDetailDto.setCreatedDate(acBuildPartReturnDto.getCreatedDate());
    }

    private AcBuildPartReturnDto getPartInactiveInfo(Long partId, Long serialId) {
        Optional<AcBuildPartReturnDto> acBuildPartReturnDto = aircraftBuildIService.getAcBuildPartReturn(partId, serialId);
        return acBuildPartReturnDto.orElseGet(AcBuildPartReturnDto::new);
    }
}
