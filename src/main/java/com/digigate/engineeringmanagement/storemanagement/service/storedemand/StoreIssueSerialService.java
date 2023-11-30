package com.digigate.engineeringmanagement.storemanagement.service.storedemand;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.impl.RoleServiceImpl;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import com.digigate.engineeringmanagement.planning.entity.Part;
import com.digigate.engineeringmanagement.storemanagement.converter.IssueSerialConverter;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssueItem;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssueSerial;
import com.digigate.engineeringmanagement.storemanagement.entity.storedemand.StorePartSerial;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreIssueSerialProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.GrnAndSerialDto;
import com.digigate.engineeringmanagement.storemanagement.repository.storedemand.StoreIssueSerialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StoreIssueSerialService {

    private final StoreIssueSerialRepository storeIssueSerialRepository;

    protected static final Logger LOGGER = LoggerFactory.getLogger(RoleServiceImpl.class);

    public StoreIssueSerialService(StoreIssueSerialRepository storeIssueSerialRepository) {
        this.storeIssueSerialRepository = storeIssueSerialRepository;
    }

    protected void convertAndSaveEntity(StoreIssueItem storeIssueItem, Part part, StorePartSerial storePartSerial,
                                        GrnAndSerialDto grnAndSerialDto) {
        List<StoreIssueSerial> storeIssueItemList = storeIssueSerialRepository.findAllByIsActiveTrue();
        validateSerial(part, storePartSerial, grnAndSerialDto,storeIssueItemList);
        StoreIssueSerial storeIssueSerial = IssueSerialConverter.convertToEntity(storeIssueItem, storePartSerial, grnAndSerialDto);
        saveItem(storeIssueSerial);
    }

    private void validateSerial(Part part, StorePartSerial storePartSerial, GrnAndSerialDto grnAndSerialDto,
                                List<StoreIssueSerial> storeIssueSerialList) {
        if (storePartSerial.notExistsInStore()) {
            throw EngineeringManagementServerException.notFound(ErrorId.STORE_PART_SERIAL_IS_NOT_FOUND);
        }

        if (part.getClassification() == PartClassification.CONSUMABLE && grnAndSerialDto.getQuantity() > storePartSerial.getQuantity()) {
            throw EngineeringManagementServerException.notFound(ErrorId.STOCK_NOT_AVAILABLE);
        }

        storeIssueSerialList.stream()
                .filter(storeIssueSerial -> Objects.equals(storeIssueSerial.getStorePartSerial().getId(),
                        storePartSerial.getId())).forEach(storeIssueSerial -> {
                            throw EngineeringManagementServerException.notFound(ErrorId.CAN_NOT_CREATE_ISSUE_BECAUSE_THIS_SERIAL_HAS_ALREADY_ISSUED);
        });
    }

    public StoreIssueSerial saveItem(StoreIssueSerial entity) {
        try {
            return storeIssueSerialRepository.save(entity);
        } catch (Exception e) {
            String name = entity.getClass().getSimpleName();
            LOGGER.error("Save failed for entity {}", name);
            LOGGER.error("Error message: {}", e.getMessage());
            throw EngineeringManagementServerException.dataSaveException(Helper.createDynamicCode(ErrorId.DATA_NOT_SAVED_DYNAMIC,
                    name));
        }
    }

    public List<StoreIssueSerialProjection> findStoreIssueSerialByStoreIssueItemIdIn(Set<Long> storeIssueItemIds) {
        return storeIssueSerialRepository.findStoreIssueSerialByStoreIssueItemIdIn(storeIssueItemIds);
    }
 public List<StoreIssueSerial> findAllByStoreIssueItemIdIn(Set<Long> storeIssueItemIds) {
        return storeIssueSerialRepository.findAllByStoreIssueItemIdIn(storeIssueItemIds);
    }

    public void deleteAllByStoreIssueItemIdIn(Set<Long> ids) {
        storeIssueSerialRepository.deleteAllByStoreIssueItemIdIn(ids);
    }

    public void updateActiveStatus(List<StoreIssueSerial> storeIssueSerials, Boolean isActive) {
        List<StoreIssueSerial> updatedSerials = storeIssueSerials.stream().peek(storeIssueSerial -> storeIssueSerial.setIsActive(isActive)).collect(Collectors.toList());
        storeIssueSerialRepository.saveAll(updatedSerials);
    }
}
