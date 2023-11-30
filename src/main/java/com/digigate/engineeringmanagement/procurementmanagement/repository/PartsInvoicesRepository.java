package com.digigate.engineeringmanagement.procurementmanagement.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.constant.PartsInVoiceWorkFlowType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.entity.PartsInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PartsInvoicesRepository extends AbstractRepository<PartsInvoice> {
    Page<PartsInvoice> findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndInvoiceNoContainsAndRfqTypeAndWorkFlowType
            (Boolean isActive, Set<Long> pendingSearchWorkFlowIds, String query, RfqType rfqType, PartsInVoiceWorkFlowType partsInVoiceWorkFlowType, Pageable pageable);

    Page<PartsInvoice> findAllByIsActiveAndWorkFlowActionIdAndInvoiceNoContainsAndRfqType
            (Boolean isActive, Long approvedId, String query, RfqType rfqType, Pageable pageable);

    Page<PartsInvoice> findAllByIsRejectedTrueAndInvoiceNoContainsAndRfqTypeAndWorkFlowType
            (String query, RfqType rfqType,PartsInVoiceWorkFlowType partsInVoiceWorkFlowType, Pageable pageable);

    Page<PartsInvoice> findAllByIsActiveAndInvoiceNoContainsAndRfqType(Boolean isActive, String query, RfqType rfqType, Pageable pageable);

    Optional<PartsInvoice> findByInvoiceNo(String invoiceNo);

    List<PartsInvoice> findByPartOrderId(Long id);
}
