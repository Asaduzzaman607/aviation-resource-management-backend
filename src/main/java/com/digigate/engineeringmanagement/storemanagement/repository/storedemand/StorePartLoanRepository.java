package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StorePartLoan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface StorePartLoanRepository extends AbstractRepository<StorePartLoan> {
    List<StorePartLoan> findByLoanNo(String loanNo);

    Page<StorePartLoan> findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndLoanNoContains
            (Boolean isActive, Set<Long> workflowIds, String query, Pageable pageable);

    Page<StorePartLoan> findAllByIsActiveAndLoanNoContains
            (Boolean isActive, String query, Pageable pageable);

    Page<StorePartLoan> findAllByIsActiveAndWorkFlowActionIdAndLoanNoContains
            (Boolean isActive, Long approvedId, String query, Pageable pageable);

    Page<StorePartLoan> findAllByIsRejectedTrueAndLoanNoContains(String query, Pageable pageable);
}
