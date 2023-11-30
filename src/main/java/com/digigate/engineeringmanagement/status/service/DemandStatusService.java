package com.digigate.engineeringmanagement.status.service;


import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.status.dto.request.DemandStatusRequestDto;
import com.digigate.engineeringmanagement.status.entity.DemandStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DemandStatusService {

    Optional<DemandStatus> findByPartIdAndChildId(Long partId, Long childId);

    void create(Long partId, Long parentId, Long demandId, Long childId,
                Integer quantity, Long workFlowActionId, VoucherType voucherType,
                boolean isActive, String module);


    void createPO(Long partId, Long parentId, Long demandId, Long childId, Long vendorQuotationInvoiceDetailId, Integer quantity,
                  Long workFlowActionId, VoucherType voucherType, String module, boolean isActive, InputType inputType,boolean isRejected);

    /**
     * for make decision update
     */
    void update(Long partId, Long childId, Long workFlowActionId,
                Boolean isRejected, VoucherType voucherType, String module);


    void updateWithPO(Long partId, Long childId, Long workFlowActionId,
                      Long vendorQuotationInvoiceDetailId, Boolean isRejected,
                      VoucherType voucherType, String module);

    void updateWithPO(Long demandId, Long partId, Long childId, Long workFlowActionId,
                      Long vendorQuotationInvoiceDetailId, Boolean isRejected,
                      VoucherType voucherType, String module);

    /**
     * for entity update
     */

    void  entityUpdate(Long partId, Long parentId, Long demandId, Long childId, Integer quantity,
                      Long workFlowActionId, VoucherType voucherType, boolean isActive, String module);

    void  entityUpdateWithRejectStatus(Long partId, Long parentId, Long demandId, Long childId, Integer quantity,
                       Long workFlowActionId, VoucherType voucherType, boolean isActive, String module,boolean isRejected);

    PageData getDemandStatusInfo(DemandStatusRequestDto demandStatusRequestDto, Pageable pageable);

    /**
     * for multiple workflow
     */
    void updateWithWft(Long partId, Long childId, Long workFlowActionId,
                       Boolean isRejected, VoucherType cs, String workFlowType); // multiple workflow

    void updateActiveStatus(Long demandId, Long childId, Long partId, VoucherType voucherType,
                            Boolean isActive, Long workflowActionId);

    void updateActiveStatusForCS(Long demandId, Long childId, Long partId, VoucherType voucherType,
                                 Long vendorQuotationInvoiceDetailId, Boolean isActive, Long workflowActionId);

    void updateRejectedStatus(Long demandId, Long childId, Long partId, VoucherType voucherType, Boolean isRejected);

    void updateRejectedStatusForCS(Long demandId, Long childId, Long partId, VoucherType voucherType,
                                   Long vendorQuotationInvoiceDetailId, Boolean isRejected, Long workflowActionId);

    void updateActiveStatusForPO(Long demandId, Long childId, Long partId, VoucherType voucherType,
                                 Long vendorQuotationInvoiceDetailId, Boolean isActive, Long workflowActionId, String module);

    void updateRejectedStatusForPO(Long demandId, Long childId, Long partId, VoucherType voucherType,
                                   Long vendorQuotationInvoiceDetailId, Boolean isRejected, Long workflowActionId, String module);

    void entityUpdateForPO(Long partId, Long parentId, Long demandId, Long childId,
                           Long vendorQuotationInvoiceDetailId, Integer quantity,
                           Long workFlowActionId, VoucherType voucherType,
                           String module, boolean isActive, InputType inputType,boolean isRejected);

    void entityUpdateWithVQDetailsId(Long partId, Long parentId, Long demandId, Long childId, Long vendorQuotationInvoiceDetailId,
                                     Integer quantity, Long workFlowActionId, VoucherType voucherType, String module);

    void createWithVQDetailsId(Long partId, Long parentId, Long demandId, Long childId,Long vendorQuotationInvoiceDetailId,
                               Integer quantity, Long workFlowActionId, VoucherType voucherType, String module);

    void updateWithWftAndVQDetailsId(Long partId, Long childId,Long vendorQuotationInvoiceDetailId,
                                     Long workFlowActionId, Boolean isRejected, VoucherType cs, String workFlowType); // multiple workflow

    List<DemandStatus> findByChildIdAndVoucherTypeAndWorkFlowType(Long piId, VoucherType pi, String workflowType);
}

