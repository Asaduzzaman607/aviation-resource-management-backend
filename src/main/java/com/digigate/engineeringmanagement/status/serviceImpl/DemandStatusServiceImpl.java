package com.digigate.engineeringmanagement.status.serviceImpl;

import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.status.dto.request.DemandStatusRequestDto;
import com.digigate.engineeringmanagement.status.entity.DemandStatus;
import com.digigate.engineeringmanagement.status.repository.DemandStatusRepository;
import com.digigate.engineeringmanagement.status.service.DemandStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class DemandStatusServiceImpl implements DemandStatusService {

    private final DemandStatusRepository demandStatusRepository;

    public DemandStatusServiceImpl(DemandStatusRepository demandStatusRepository) {
        this.demandStatusRepository = demandStatusRepository;
    }


    @Override
    public Optional<DemandStatus> findByPartIdAndChildId(Long partId, Long childId) {
        return demandStatusRepository.findByPartIdAndChildId(partId, childId);
    }

    @Override
    public void create(Long partId,
                       Long parentId,
                       Long demandId,
                       Long childId,
                       Integer quantity,
                       Long workFlowActionId,
                       VoucherType voucherType,
                       boolean isActive,
                       String module) {
        DemandStatus demandStatus = new DemandStatus();
        demandStatus.setParentId(parentId);
        demandStatus.setDemandId(demandId);
        demandStatus.setChildId(childId);
        demandStatus.setPartId(partId);
        demandStatus.setQuantity(quantity);
        demandStatus.setWorkFlowActionId(workFlowActionId);
        demandStatus.setVoucherType(voucherType);
        demandStatus.setIsActiveStatus(isActive);
        demandStatus.setModule(module);
        demandStatusRepository.save(demandStatus);
    }

    @Override
    public void createPO(Long partId, Long parentId, Long demandId, Long childId, Long vendorQuotationInvoiceDetailId, Integer quantity, Long workFlowActionId,
                         VoucherType voucherType, String module, boolean isActive, InputType inputType, boolean isRejected) {
        DemandStatus demandStatus = new DemandStatus();
        demandStatus.setParentId(parentId);
        demandStatus.setDemandId(demandId);
        demandStatus.setChildId(childId);
        demandStatus.setPartId(partId);
        demandStatus.setQuantity(quantity);
        demandStatus.setVendorQuotationInvoiceDetailsId(vendorQuotationInvoiceDetailId);
        demandStatus.setWorkFlowActionId(workFlowActionId);
        demandStatus.setVoucherType(voucherType);
        demandStatus.setModule(module);
        demandStatus.setIsActiveStatus(isActive);
        demandStatus.setIsRejected(isRejected);
        demandStatus.setInputType(inputType);
        demandStatusRepository.save(demandStatus);
    }

    @Override
    public void update(Long partId, Long childId, Long workFlowActionId, Boolean isRejected, VoucherType voucherType, String module) {
        try {
            DemandStatus demandStatus = demandStatusRepository.findByPartIdAndChildIdAndVoucherType(partId, childId, voucherType);
            if (Objects.nonNull(demandStatus)) {
                demandStatus.setIsRejected(isRejected);
                demandStatus.setWorkFlowActionId(workFlowActionId);
                demandStatus.setModule(module);
                demandStatusRepository.save(demandStatus);
            } else {
                log.error("demand status not found");
            }
        } catch (Exception e) {
            log.error("Demand status not found: {}", e.getMessage());
        }
    }

    @Override
    public void updateWithPO(Long partId, Long childId, Long workFlowActionId, Long vendorQuotationInvoiceDetailId, Boolean isRejected, VoucherType voucherType, String module) {
        try {
            DemandStatus demandStatus = demandStatusRepository.findByPartIdAndChildIdAndVoucherTypeAndVendorQuotationInvoiceDetailsIdAndModule(partId, childId, vendorQuotationInvoiceDetailId, module);
            if (Objects.nonNull(demandStatus)) {
                demandStatus.setIsRejected(isRejected);
                demandStatus.setWorkFlowActionId(workFlowActionId);
                demandStatus.setModule(module);
                demandStatusRepository.save(demandStatus);
            } else {
                log.error("Demand status not found");
            }
        } catch (Exception e) {
            log.error("Demand status not found {}", e.getMessage());
        }
    }

    @Override
    public void updateWithPO(Long demandId, Long partId, Long childId, Long workFlowActionId, Long vendorQuotationInvoiceDetailId, Boolean isRejected, VoucherType voucherType, String module) {
        try {
            DemandStatus demandStatus = demandStatusRepository.findByDemandIdAndPartIdAndChildIdAndVoucherTypeAndVendorQuotationInvoiceDetailsId(demandId, partId, childId, voucherType, vendorQuotationInvoiceDetailId);
            if (Objects.nonNull(demandStatus)) {
                demandStatus.setIsRejected(isRejected);
                demandStatus.setWorkFlowActionId(workFlowActionId);
                demandStatus.setModule(module);
                demandStatusRepository.save(demandStatus);
            } else {
                log.error("Demand status not found");
            }
        } catch (Exception e) {
            log.error("Demand status not found {}", e.getMessage());
        }
    }

    @Override
    public void entityUpdate(Long partId,
                             Long parentId,
                             Long demandId,
                             Long childId,
                             Integer quantity,
                             Long workFlowActionId,
                             VoucherType voucherType,
                             boolean isActive,
                             String module) {

        create(partId, parentId, demandId, childId, quantity, workFlowActionId, voucherType, isActive, module);
    }

    @Override
    public void entityUpdateWithRejectStatus(Long partId, Long parentId, Long demandId, Long childId, Integer quantity, Long workFlowActionId, VoucherType voucherType, boolean isActive, String module, boolean isRejected) {
        createWithRejectedSts(partId, parentId, demandId, childId, quantity, workFlowActionId, voucherType, isActive, module, isRejected);
    }

    private void createWithRejectedSts(Long partId, Long parentId, Long demandId, Long childId, Integer quantity, Long workFlowActionId, VoucherType voucherType, boolean isActive, String module, boolean isRejected) {
        DemandStatus demandStatus = new DemandStatus();
        demandStatus.setParentId(parentId);
        demandStatus.setDemandId(demandId);
        demandStatus.setChildId(childId);
        demandStatus.setPartId(partId);
        demandStatus.setQuantity(quantity);
        demandStatus.setWorkFlowActionId(workFlowActionId);
        demandStatus.setVoucherType(voucherType);
        demandStatus.setIsActiveStatus(isActive);
        demandStatus.setModule(module);
        demandStatus.setIsRejected(isRejected);
        demandStatusRepository.save(demandStatus);
    }

    @Override
    public void entityUpdateForPO(Long partId, Long parentId, Long demandId, Long childId, Long vendorQuotationInvoiceDetailId, Integer quantity, Long workFlowActionId,
                                  VoucherType voucherType, String module, boolean isActive, InputType inputType, boolean isRejected) {
        createPO(partId, parentId, demandId, childId, vendorQuotationInvoiceDetailId, quantity, workFlowActionId, voucherType, module, isActive, inputType, isRejected);
    }

    @Override
    public void entityUpdateWithVQDetailsId(Long partId, Long parentId, Long demandId, Long childId, Long vendorQuotationInvoiceDetailId, Integer quantity, Long workFlowActionId, VoucherType voucherType, String module) {
        createWithVQDetailsId(partId, parentId, demandId, childId, vendorQuotationInvoiceDetailId, quantity, workFlowActionId, voucherType, module);
    }

    @Override
    public void createWithVQDetailsId(Long partId, Long parentId, Long demandId, Long childId, Long vendorQuotationInvoiceDetailId, Integer quantity, Long workFlowActionId, VoucherType voucherType, String module) {
        DemandStatus demandStatus = new DemandStatus();
        demandStatus.setParentId(parentId);
        demandStatus.setDemandId(demandId);
        demandStatus.setChildId(childId);
        demandStatus.setPartId(partId);
        demandStatus.setQuantity(quantity);
        demandStatus.setVendorQuotationInvoiceDetailsId(vendorQuotationInvoiceDetailId);
        demandStatus.setWorkFlowActionId(workFlowActionId);
        demandStatus.setVoucherType(voucherType);
        demandStatus.setModule(module);
        demandStatusRepository.save(demandStatus);
    }

    @Override
    public void updateWithWftAndVQDetailsId(Long partId, Long childId, Long vendorQuotationInvoiceDetailId, Long workFlowActionId, Boolean isRejected, VoucherType voucherType, String workFlowType) {
        try {
            DemandStatus demandStatus = demandStatusRepository.findByPartIdAndChildIdAndVoucherTypeAndVendorQuotationInvoiceDetailsId(partId, childId, voucherType, vendorQuotationInvoiceDetailId);
            if (Objects.nonNull(demandStatus)) {
                demandStatus.setIsRejected(isRejected);
                demandStatus.setWorkFlowActionId(workFlowActionId);
                if (Objects.nonNull(workFlowType)) {
                    demandStatus.setWorkFlowType(workFlowType);
                }
                demandStatusRepository.save(demandStatus);
            } else {
                log.error("Demand status not found");
            }
        } catch (Exception e) {
            log.error("Demand status not found: {}", e.getMessage());
        }
    }

    @Override
    public List<DemandStatus> findByChildIdAndVoucherTypeAndWorkFlowType(Long piId, VoucherType voucherType, String workflowType) {
        return demandStatusRepository.findByChildIdAndVoucherTypeAndWorkFlowType(piId, voucherType, workflowType);
    }

    public void deleteAllDemandStatus(Long demandId, Long childId, VoucherType voucherType) {
        demandStatusRepository.deleteAllByDemandIdAndChildIdAndVoucherType(demandId, childId, voucherType);
    }

    public void deleteAllDemandStatusForPO(Long demandId, Long childId, Long vendorQuotationInvoiceDetailId, VoucherType voucherType) {
        demandStatusRepository.deleteAllByDemandIdAndChildIdAndVendorQuotationInvoiceDetailsIdAndVoucherType(demandId, childId, vendorQuotationInvoiceDetailId, voucherType);
    }

    @Override
    public PageData getDemandStatusInfo(DemandStatusRequestDto demandStatusRequestDto, Pageable pageable) {
        if (Objects.isNull(demandStatusRequestDto.getPartId())) {
            return Helper.buildCustomPagedData(demandStatusRepository.findPartStatusByDemand(demandStatusRequestDto.getDemandId()), pageable);
        } else {
            return Helper.buildCustomPagedData(demandStatusRepository.findPartStatusByDemandIdAndPartId(demandStatusRequestDto.getDemandId(), demandStatusRequestDto.getPartId()), pageable);
        }
    }

    @Override
    public void updateWithWft(Long partId,
                              Long childId,
                              Long workFlowActionId,
                              Boolean isRejected,
                              VoucherType voucherType,
                              String workFlowType) {
        try {
            DemandStatus demandStatus = demandStatusRepository.findByPartIdAndChildIdAndVoucherType(partId, childId, voucherType);
            if (Objects.nonNull(demandStatus)) {
                demandStatus.setIsRejected(isRejected);
                demandStatus.setWorkFlowActionId(workFlowActionId);
                if (Objects.nonNull(workFlowType)) {
                    demandStatus.setWorkFlowType(workFlowType);
                }
                demandStatusRepository.save(demandStatus);
            } else {
                log.error("Demand status not found");
            }
        } catch (Exception e) {
            log.error("Demand status not found: {}", e.getMessage());
        }
    }

    @Override
    public void updateActiveStatus(Long demandId, Long childId, Long partId, VoucherType voucherType, Boolean isActive, Long workFlowActionId) {
        try {
            DemandStatus demandStatus = demandStatusRepository.
                    findByDemandIdAndChildIdAndPartIdAndVoucherType(demandId, childId, partId, voucherType);

            demandStatus.setIsActiveStatus(isActive);
            demandStatus.setWorkFlowActionId(workFlowActionId);
            demandStatusRepository.save(demandStatus);
        } catch (Exception e) {
            log.error("Demand status not found");
        }
    }

    @Override
    public void updateActiveStatusForCS(Long demandId, Long childId, Long partId, VoucherType voucherType, Long vendorQuotationInvoiceDetailId, Boolean isActive, Long workflowActionId) {
        try {
            DemandStatus demandStatus = demandStatusRepository.
                    findByDemandIdAndChildIdAndPartIdAndVoucherTypeAndVendorQuotationInvoiceDetailsId(demandId, childId, partId, voucherType, vendorQuotationInvoiceDetailId);

            demandStatus.setIsActiveStatus(isActive);
            demandStatus.setWorkFlowActionId(workflowActionId);
            demandStatusRepository.save(demandStatus);
        } catch (Exception e) {
            log.error("Demand status not found: {}", e.getMessage());
        }
    }

    @Override
    public void updateRejectedStatus(Long demandId, Long childId, Long partId, VoucherType voucherType, Boolean isRejected) {
        try {
            DemandStatus demandStatus = demandStatusRepository.
                    findByDemandIdAndChildIdAndPartIdAndVoucherType(demandId, childId, partId, voucherType);

            demandStatus.setIsRejected(isRejected);
            demandStatusRepository.save(demandStatus);
        } catch (Exception e) {
            log.error("Demand status not found: {}", e.getMessage());
        }
    }

    @Override
    public void updateRejectedStatusForCS(Long demandId, Long childId, Long partId, VoucherType voucherType, Long vendorQuotationInvoiceDetailId, Boolean isRejected, Long workflowActionId) {
        try {
            DemandStatus demandStatus = demandStatusRepository.
                    findByDemandIdAndChildIdAndPartIdAndVoucherTypeAndVendorQuotationInvoiceDetailsId(demandId, childId, partId, voucherType, vendorQuotationInvoiceDetailId);

            demandStatus.setIsRejected(isRejected);
            demandStatusRepository.save(demandStatus);
        } catch (Exception e) {
            log.error("Demand status not found: {}", e.getMessage());
        }
    }

    @Override
    public void updateActiveStatusForPO(Long demandId, Long childId, Long partId, VoucherType voucherType, Long vendorQuotationInvoiceDetailId, Boolean isActive, Long workflowActionId, String module) {
        try {
            DemandStatus demandStatus = demandStatusRepository.
                    findByDemandIdAndChildIdAndPartIdAndVoucherTypeAndVendorQuotationInvoiceDetailsIdAndModule(demandId, childId, partId, voucherType, vendorQuotationInvoiceDetailId, module);

            demandStatus.setIsActiveStatus(isActive);
            demandStatus.setWorkFlowActionId(workflowActionId);
            demandStatusRepository.save(demandStatus);
        } catch (Exception e) {
            log.error("Demand status not found: {}", e.getMessage());
        }
    }

    @Override
    public void updateRejectedStatusForPO(Long demandId, Long childId, Long partId, VoucherType voucherType,
                                          Long vendorQuotationInvoiceDetailId, Boolean isRejected, Long workflowActionId, String module) {
        try {
            DemandStatus demandStatus = demandStatusRepository.
                    findByDemandIdAndChildIdAndPartIdAndVoucherTypeAndVendorQuotationInvoiceDetailsIdAndModule(demandId, childId, partId, voucherType, vendorQuotationInvoiceDetailId, module);

            demandStatus.setIsRejected(isRejected);
            demandStatusRepository.save(demandStatus);
        } catch (Exception e) {
            log.error("Demand status not found: {}", e.getMessage());
        }
    }

}
