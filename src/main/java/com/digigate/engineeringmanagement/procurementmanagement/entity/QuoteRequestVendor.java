package com.digigate.engineeringmanagement.procurementmanagement.entity;

import com.digigate.engineeringmanagement.common.entity.AbstractDomainBasedEntity;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Quote Request Vendor Entity
 *
 * @author Sayem Hasnat
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "quote_request_vendors")
public class QuoteRequestVendor extends AbstractDomainBasedEntity {
    @Column(nullable = false)
    private LocalDate requestDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_request_id", nullable = false)
    private QuoteRequest quoteRequest;
    @Column(name = "quote_request_id", updatable = false, insertable = false)
    private Long quoteRequestId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;
    @Column(name = "vendor_id", insertable = false, updatable = false)
    private Long vendorId;
}
