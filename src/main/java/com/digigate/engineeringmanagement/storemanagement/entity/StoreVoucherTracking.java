package com.digigate.engineeringmanagement.storemanagement.entity;

import com.digigate.engineeringmanagement.common.constant.VoucherType;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "store_voucher_tracking_log")
public class StoreVoucherTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private VoucherType voucherType;
    @Column(columnDefinition = "bigint default 0", nullable = false)
    private Long voucherLastSeq;
    private Long parentId;
    private String parentVoucher;
    @Column(columnDefinition = "bigint default 0", nullable = false)
    private Long voucherChildLastSeq;

}
