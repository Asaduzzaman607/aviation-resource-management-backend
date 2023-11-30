package com.digigate.engineeringmanagement.storemanagement.repository;

import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.storemanagement.entity.StoreVoucherTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface StoreVoucherTrackingRepository extends JpaRepository<StoreVoucherTracking, Long> {
    Optional<StoreVoucherTracking> findByParentIdAndVoucherTypeAndParentVoucher(Long parentId, VoucherType voucherType, String parentVoucher);

    Optional<StoreVoucherTracking> findByVoucherTypeAndParentVoucherIsNull(VoucherType voucherType);

}