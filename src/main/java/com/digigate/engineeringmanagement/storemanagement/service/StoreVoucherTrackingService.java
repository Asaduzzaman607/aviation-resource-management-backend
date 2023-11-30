package com.digigate.engineeringmanagement.storemanagement.service;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VoucherType;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.util.Helper;
import com.digigate.engineeringmanagement.storemanagement.entity.StoreVoucherTracking;
import com.digigate.engineeringmanagement.storemanagement.repository.StoreVoucherTrackingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
public class StoreVoucherTrackingService {
    private final StoreVoucherTrackingRepository voucherTrackingRepository;

    public StoreVoucherTrackingService(StoreVoucherTrackingRepository voucherTrackingRepository) {
        this.voucherTrackingRepository = voucherTrackingRepository;
    }

    public String generateUniqueNo(VoucherType key) {
        return key.toString() +
                ApplicationConstant.DASH +
                generateUniqueNoForPartOrderService(key) +
                ApplicationConstant.SLASH +
                LocalDate.now().getYear();
    }

    public String generateUniqueNoForPartOrderService(VoucherType key) {
        return String.format(ApplicationConstant.FOUR_DIGIT_FORMAT, getVoucherLastSeq(key));
    }

    public String generateUniqueVoucherNo(Long parentId, VoucherType voucherType, String parentVoucher) {
        if(Objects.isNull(parentId)){
            return ApplicationConstant.EMPTY_STRING;
        }

        return voucherType.toString() +
                ApplicationConstant.DASH +
                splitVoucherNo(parentVoucher) +
                ApplicationConstant.DASH +
                generateUniqueChildNo(parentId, voucherType, parentVoucher);
    }

    public String generateUniqueChildNo(Long parentId, VoucherType voucherType, String parentVoucher) {
        Long lastSeq = getVoucherChildLastSeq(parentId, voucherType, parentVoucher);
        if (lastSeq > ApplicationConstant.INT_ONE) {
            return String.format(String.valueOf(lastSeq)) + ApplicationConstant.REVISED;
        }
        return String.format(String.valueOf(lastSeq));
    }

    private String splitVoucherNo(String parentVoucher) {
        return parentVoucher.chars()
                .skip(parentVoucher.indexOf(ApplicationConstant.DASH) + ApplicationConstant.INT_ONE)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }

    /**
     * This Method is responsible for return Voucher Last Sequence and increment Voucher Last Sequence by 1 in DB
     *
     * @param voucherType {@link VoucherType}
     * @return voucherLastSeq {@link Long}
     */
    private Long getVoucherLastSeq(VoucherType voucherType) {
        StoreVoucherTracking storeVoucherTrackingLog = voucherTrackingRepository
                .findByVoucherTypeAndParentVoucherIsNull(voucherType).orElseThrow(() ->
                        EngineeringManagementServerException.internalServerException(Helper
                                .createDynamicCode(ErrorId.VOUCHER_ERROR, voucherType.name())));

        Long incrementedSeq = storeVoucherTrackingLog.getVoucherLastSeq() + 1;
        storeVoucherTrackingLog.setVoucherLastSeq(incrementedSeq);

        synchronized (voucherType) {
            try {
                voucherTrackingRepository.saveAndFlush(storeVoucherTrackingLog);
            } catch (Exception e) {
                throw EngineeringManagementServerException.dataSaveException(ErrorId.VOUCHER_NO_ERROR);
            }
            return storeVoucherTrackingLog.getVoucherLastSeq();
        }
    }

    private Long getVoucherChildLastSeq(Long parentId, VoucherType voucherType, String parentVoucher) {
        Optional<StoreVoucherTracking> storeVoucherTracking = voucherTrackingRepository
                .findByParentIdAndVoucherTypeAndParentVoucher(parentId, voucherType, parentVoucher);

        StoreVoucherTracking entityInst;

        if (storeVoucherTracking.isPresent()) {
            entityInst = storeVoucherTracking.get();
            Long incrementVal = entityInst.getVoucherChildLastSeq() + ApplicationConstant.INT_ONE;
            entityInst.setVoucherChildLastSeq(incrementVal);

        } else {
            entityInst = new StoreVoucherTracking();
            entityInst.setVoucherType(voucherType);
            entityInst.setParentId(parentId);
            entityInst.setVoucherChildLastSeq(1L);
            entityInst.setVoucherLastSeq(0L);
            entityInst.setParentVoucher(parentVoucher);
        }
        synchronized (voucherType) {
            try {
                voucherTrackingRepository.saveAndFlush(entityInst);
            } catch (Exception e) {
                throw EngineeringManagementServerException.dataSaveException(ErrorId.VOUCHER_NO_ERROR);
            }
            return entityInst.getVoucherChildLastSeq();
        }
    }
}
