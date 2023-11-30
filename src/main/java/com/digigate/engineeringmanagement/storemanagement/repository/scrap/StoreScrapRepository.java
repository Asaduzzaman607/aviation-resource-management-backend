package com.digigate.engineeringmanagement.storemanagement.repository.scrap;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface StoreScrapRepository extends AbstractRepository<StoreScrap> {
    Page<StoreScrap> findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndVoucherNoContains
            (Boolean isActive, Set<Long> pendingSearchWorkFlowIds, String query, Pageable pageable);

    Page<StoreScrap> findAllByIsActiveAndWorkFlowActionIdAndVoucherNoContains
            (Boolean isActive, Long approvedId, String query, Pageable pageable);

    Page<StoreScrap> findAllByIsRejectedTrueAndVoucherNoContains(String query, Pageable pageable);

    Page<StoreScrap> findAllByIsActiveAndVoucherNoContains(Boolean isActive, String query, Pageable pageable);
}
