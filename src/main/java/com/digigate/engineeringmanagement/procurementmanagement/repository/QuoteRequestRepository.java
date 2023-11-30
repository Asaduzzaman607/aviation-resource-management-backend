package com.digigate.engineeringmanagement.procurementmanagement.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.QuoteRequestProjection;
import com.digigate.engineeringmanagement.procurementmanagement.entity.QuoteRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.Optional;

@Repository
public interface QuoteRequestRepository extends AbstractRepository<QuoteRequest> {
    boolean existsByRequisitionIdAndIsActiveTrue(Long id);

    Optional<QuoteRequestProjection> findQuoteRequestById(Long id);

    List<QuoteRequestProjection> findQuoteRequestByIdIn(Set<Long> idList);

    @Query(value = "SELECT qr.* FROM quote_requests qr " +
            "INNER JOIN vendor_quotations vq ON vq.rfq_id = qr.id " +
            "WHERE vq.part_order_id = :id", nativeQuery = true)
    QuoteRequest findQuoteRequestByPartOrderId(@Param("id") Long id);
}
