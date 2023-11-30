package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.service.AbstractService;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.UnitMeasurement;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartAvailabilityLog;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartAvailabilityLogRequestDto;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StorePartSerialInternalDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.StorePartAvailabilityLogRepository;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.UnitMeasurementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StorePartAvailabilityLogService extends AbstractService<StorePartAvailabilityLog,
        StorePartAvailabilityLogRequestDto> {
    private final StorePartSerialService storePartSerialService;
    private final UnitMeasurementService unitMeasurementService;

    public StorePartAvailabilityLogService(StorePartAvailabilityLogRepository storePartAvailabilityLogRepository,
                                           StorePartSerialService storePartSerialService,
                                           UnitMeasurementService unitMeasurementService) {
        super(storePartAvailabilityLogRepository);
        this.storePartSerialService = storePartSerialService;
        this.unitMeasurementService = unitMeasurementService;
    }

    @Transactional
    public StorePartAvailabilityLog create(StorePartAvailabilityLogRequestDto storePartAvailabilityLogRequestDto){
        StorePartAvailabilityLog storePartAvailabilityLog = convertToEntity(storePartAvailabilityLogRequestDto);
        return super.saveItem(storePartAvailabilityLog);
    }

    @Override
    protected <T> T convertToResponseDto(StorePartAvailabilityLog storePartAvailabilityLog) {
        return null;
    }

    @Override
    protected StorePartAvailabilityLog convertToEntity(StorePartAvailabilityLogRequestDto storePartAvailabilityLogRequestDto) {
        StorePartAvailabilityLog storePartAvailabilityLog = new StorePartAvailabilityLog();

        UnitMeasurement uom = unitMeasurementService.findById(storePartAvailabilityLogRequestDto.getUomId());
        storePartAvailabilityLog.setQuantity(storePartAvailabilityLogRequestDto.getQuantity());
        storePartAvailabilityLog.setParentType(storePartAvailabilityLogRequestDto.getParentType());
        storePartAvailabilityLog.setPartStatus(storePartAvailabilityLogRequestDto.getPartStatus());
        storePartAvailabilityLog.setParentId(storePartAvailabilityLogRequestDto.getParentId());
        storePartAvailabilityLog.setInStock(storePartAvailabilityLogRequestDto.getInStock());
        storePartAvailabilityLog.setIssuedQty(storePartAvailabilityLogRequestDto.getIssuedQty());
        storePartAvailabilityLog.setReceivedQty(storePartAvailabilityLogRequestDto.getReceivedQty());
        storePartAvailabilityLog.setVoucherNo(storePartAvailabilityLogRequestDto.getVoucherNo());
        storePartAvailabilityLog.setReceiveDate(storePartAvailabilityLogRequestDto.getReceiveDate());
        storePartAvailabilityLog.setShelfLife(storePartAvailabilityLogRequestDto.getShelfLife());
        storePartAvailabilityLog.setExpiryDate(storePartAvailabilityLogRequestDto.getExpiryDate());
        storePartAvailabilityLog.setUnitPrice(storePartAvailabilityLogRequestDto.getUnitPrice());
        storePartAvailabilityLog.setIssuedAc(storePartAvailabilityLogRequestDto.getIssuedAc());
        storePartAvailabilityLog.setLocation(storePartAvailabilityLogRequestDto.getLocation());
        storePartAvailabilityLog.setTransactionType(storePartAvailabilityLogRequestDto.getTransactionType());
        storePartAvailabilityLog.setGrnNo(storePartAvailabilityLogRequestDto.getGrnNo());

        storePartAvailabilityLog.setStorePartSerial(storePartSerialService.findAndUpdateStorePartSerial(
                populateToSerialRequestDto(storePartAvailabilityLogRequestDto, storePartAvailabilityLog)));
        storePartAvailabilityLog.setCurrencyId(storePartAvailabilityLog.getCurrencyId());
        return storePartAvailabilityLog;
    }

    private StorePartSerialInternalDto populateToSerialRequestDto(
            StorePartAvailabilityLogRequestDto storePartAvailabilityLogRequestDto, StorePartAvailabilityLog storePartAvailabilityLog) {
        return StorePartSerialInternalDto.builder()
                .unitPrice(storePartAvailabilityLogRequestDto.getUnitPrice())
                .shelfLife(storePartAvailabilityLogRequestDto.getShelfLife())
                .expiryDate(storePartAvailabilityLogRequestDto.getExpiryDate())
                .grnNo(storePartAvailabilityLogRequestDto.getGrnNo())
                .partStatus(storePartAvailabilityLogRequestDto.getPartStatus())
                .parentType(storePartAvailabilityLogRequestDto.getParentType())
                .partNo(storePartAvailabilityLogRequestDto.getPartNo())
                .serialNo(storePartAvailabilityLogRequestDto.getSerialNo())
                .quantity(storePartAvailabilityLog.getQuantity())
                .transactionType(storePartAvailabilityLogRequestDto.getTransactionType())
                .build();
    }

    @Override
    protected StorePartAvailabilityLog updateEntity(StorePartAvailabilityLogRequestDto dto, StorePartAvailabilityLog entity) {
        return null;
    }
}
