package com.digigate.engineeringmanagement.procurementmanagement.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.QuoteRequestVendorProjection;
import com.digigate.engineeringmanagement.procurementmanagement.entity.QuoteRequestVendor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Quote Request Vendor Repository
 *
 * @author Sayem Hasnat
 */
@Repository
public interface QuoteRequestVendorRepository extends AbstractRepository<QuoteRequestVendor> {
    List<QuoteRequestVendor> findByQuoteRequestId(Long rfqId);
    List<QuoteRequestVendorProjection> findQuoteRequestVendorByIdIn(Set<Long> quoteRequestVendorIdList);
}
