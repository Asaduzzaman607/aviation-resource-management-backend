package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StorePartLoanDetails;

import java.util.List;
import java.util.Set;

public interface StorePartLoanDetailsRepository extends AbstractRepository<StorePartLoanDetails> {
    List<StorePartLoanDetails> findByStorePartLoanIdInAndIsActiveTrue(Set<Long> loanIds);
}
