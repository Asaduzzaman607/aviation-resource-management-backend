package com.digigate.engineeringmanagement.storemanagement.service.planning;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.configurationmanagement.service.aircraftinformation.AircraftService;
import com.digigate.engineeringmanagement.planning.dto.AcBuildPartReturnDto;
import com.digigate.engineeringmanagement.planning.service.impl.PositionServiceImpl;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.ReturnPartsDetail;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreReturn;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StoreReturnPart;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ReturnPartsDetailService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreReturnPartService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.StoreReturnService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class PartInactiveInfoPostService {
    private final StoreReturnService storeReturnService;
    private final StoreReturnPartService storeReturnPartService;
    private final ReturnPartsDetailService returnPartsDetailService;
    private final AircraftService aircraftService;
    private final PositionServiceImpl positionService;

    public PartInactiveInfoPostService(StoreReturnService storeReturnService,
                                       @Lazy StoreReturnPartService storeReturnPartService,
                                       ReturnPartsDetailService returnPartsDetailService,
                                       AircraftService aircraftService,
                                       PositionServiceImpl positionService) {
        this.storeReturnService = storeReturnService;
        this.storeReturnPartService = storeReturnPartService;
        this.returnPartsDetailService = returnPartsDetailService;
        this.aircraftService = aircraftService;
        this.positionService = positionService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setPartInactiveInfo(List<AcBuildPartReturnDto> acBuildPartReturnDtos){
        acBuildPartReturnDtos.forEach(this::populateInactiveInfo);
    }

    private void populateInactiveInfo(AcBuildPartReturnDto acBuildPartReturnDto) {
        Long removePartId = acBuildPartReturnDto.getPartId();
        Long planningSerialId = acBuildPartReturnDto.getSerialId();
        ReturnPartsDetail returnPartsDetail =
                returnPartsDetailService.findByRemovedPartIdAndSerialId(removePartId, planningSerialId);
        if(Objects.isNull(returnPartsDetail))return;

        StoreReturnPart storeReturnPart =
                storeReturnPartService.findByIdUnfiltered(returnPartsDetail.getStoreReturnPartId());
        StoreReturn storeReturn = storeReturnService.findByIdUnfiltered(storeReturnPart.getStoreReturnId());
        /** saving info */
        saveReturnPartDetail(returnPartsDetail, acBuildPartReturnDto);
        saveReturnPart(storeReturnPart, acBuildPartReturnDto);
        saveReturn(storeReturn, acBuildPartReturnDto);
    }

    private void saveReturn(StoreReturn storeReturn, AcBuildPartReturnDto acBuildPartReturnDto) {
        if(acBuildPartReturnDto.getIsInactive()){
            storeReturn.setActivePartCount(getUpdateCount(storeReturn));

            /** save return */
            storeReturnService.saveItem(storeReturn);
        }
    }

    private void saveReturnPart(StoreReturnPart storeReturnPart, AcBuildPartReturnDto acBuildPartReturnDto) {
        storeReturnPart.setIsInactive(acBuildPartReturnDto.getIsInactive());

        /** save return part */
        storeReturnPartService.saveItem(storeReturnPart);
    }

    private void saveReturnPartDetail(ReturnPartsDetail returnPartsDetail,
                                            AcBuildPartReturnDto acBuildPartReturnDto) {
        returnPartsDetail.setTsn(acBuildPartReturnDto.getTsn());
        returnPartsDetail.setCsn(acBuildPartReturnDto.getCsn());
        returnPartsDetail.setTsr(acBuildPartReturnDto.getTsr());
        returnPartsDetail.setCsr(acBuildPartReturnDto.getCsr());
        returnPartsDetail.setTso(acBuildPartReturnDto.getTso());
        returnPartsDetail.setCso(acBuildPartReturnDto.getCso());
        returnPartsDetail.setReasonRemoved(acBuildPartReturnDto.getRemovalReason());
        returnPartsDetail.setRemovalDate(acBuildPartReturnDto.getRemovalDate());
        returnPartsDetail.setAuthNo(acBuildPartReturnDto.getAuthNo());
        returnPartsDetail.setSign(acBuildPartReturnDto.getSign());
        returnPartsDetail.setCreatedDate(acBuildPartReturnDto.getCreatedDate());
        if(Objects.nonNull(acBuildPartReturnDto.getAircraftId())){
            returnPartsDetail.setRemovedFromAircraft(
                    aircraftService.findByIdUnfiltered(acBuildPartReturnDto.getAircraftId()));
        }
        if(Objects.nonNull(acBuildPartReturnDto.getPositionId())){
            returnPartsDetail.setPosition(
                    positionService.findByIdUnfiltered(acBuildPartReturnDto.getPositionId()));
        }

        /** save return part detail */
        returnPartsDetailService.saveItem(returnPartsDetail);
    }

    private Integer getUpdateCount(StoreReturn storeReturn) {
        return Math.max(storeReturn.getActivePartCount() - 1, ApplicationConstant.VALUE_ZERO);
    }
}
