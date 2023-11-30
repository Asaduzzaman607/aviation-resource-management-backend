package com.digigate.engineeringmanagement.storemanagement.service.scrap;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.impl.RoleServiceImpl;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.storemanagement.converter.ScrapPartAndSerialConverter;
import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrapPart;
import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrapPartSerial;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreScrapPartSerialProjection;
import com.digigate.engineeringmanagement.storemanagement.repository.scrap.StoreScrapPartSerialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class StoreScrapPartSerialService {

    private final StoreScrapPartSerialRepository repository;

    protected static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    public StoreScrapPartSerialService(StoreScrapPartSerialRepository repository) {
        this.repository = repository;
    }

    protected void convertAndSaveEntity(StoreScrapPart storeScrapPart, StorePartSerial storePartSerial, Integer quantity) {
        StoreScrapPartSerial storeScrapPartSerial = ScrapPartAndSerialConverter.convertToEntity(storeScrapPart, storePartSerial, quantity);
        saveItem(storeScrapPartSerial);
    }

    public StoreScrapPartSerial saveItem(StoreScrapPartSerial storeScrapPartSerial) {
        try {
            return repository.save(storeScrapPartSerial);
        } catch (Exception e) {
            String name = storeScrapPartSerial.getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", name);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    name));
        }
    }

    public List<StoreScrapPartSerial> findAllByStoreScrapPartId(Long partId) {
        return repository.findAllByStoreScrapPartId(partId);
    }

    public List<StoreScrapPartSerialProjection> findAllByStoreScrapPartIdIn(Set<Long> ids) {
        return repository.findAllByStoreScrapPartIdIn(ids);
    }


    public void deleteAll(Iterable<StoreScrapPartSerial> storeScrapPartSerialList) {
        repository.deleteAllInBatch(storeScrapPartSerialList);
    }
}
