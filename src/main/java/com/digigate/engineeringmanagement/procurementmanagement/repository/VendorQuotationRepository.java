package com.digigate.engineeringmanagement.procurementmanagement.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.constant.ExchangeType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.CsQuotationProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.VqProjection;
import com.digigate.engineeringmanagement.procurementmanagement.dto.response.CsQuotationViewModel;
import com.digigate.engineeringmanagement.procurementmanagement.entity.VendorQuotation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VendorQuotationRepository extends AbstractRepository<VendorQuotation> {
    List<CsQuotationProjection> findVendorQuotationByIdIn(Set<Long> quotationIdList);
    List<VqProjection> findByIdIn(Set<Long> quoteIdSet);
    List<VendorQuotation> findByPartOrderIdIn(Set<Long> ids);
    List<VendorQuotation> findByQuoteRequestIdAndQuoteRequestVendorIdAndIsActiveTrue(Long qrId, Long qrvId);
    @Query("SELECT DISTINCT new com.digigate.engineeringmanagement.procurementmanagement.dto.response.CsQuotationViewModel(" +
            "vq.id, " +
            "vq.quotationNo, " +
            "vq.date, " +
            "vq.validUntil, " +
            "v.name, " +
            "v.vendorType" +
            ") " +
            "FROM VendorQuotation vq " +
            "INNER JOIN VendorQuotationInvoiceDetail vd ON vq.id = vd.vendorQuotationInvoiceId " +
            "INNER JOIN QuoteRequestVendor q ON vq.quoteRequestVendorId = q.id " +
            "INNER JOIN Vendor v ON q.vendorId = v.id " +
            "WHERE (:exchangeType IS NULL OR vd.exchangeType = :exchangeType) " +
            "AND (:quoteRequestId IS NULL OR vq.quoteRequestId = :quoteRequestId)"
    )
    List<CsQuotationViewModel> findByCriteria(@Param("exchangeType") ExchangeType exchangeType,
                                              @Param("quoteRequestId") Long quoteRequestId);

    List<VendorQuotation> findByQuoteRequestId(Long rfqId);

    VendorQuotation findByPartOrderId(Long partOrderId);
}
