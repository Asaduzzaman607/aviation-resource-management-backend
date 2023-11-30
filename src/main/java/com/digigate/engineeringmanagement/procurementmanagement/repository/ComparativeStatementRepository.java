package com.digigate.engineeringmanagement.procurementmanagement.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.ExistingCsProjection;
import com.digigate.engineeringmanagement.procurementmanagement.entity.ComparativeStatement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ComparativeStatementRepository extends AbstractRepository<ComparativeStatement> {
    ComparativeStatement findByIdAndRfqType(Long id, RfqType rfqType);

    @Query("select cs.id as id, " +
            "cs.comparativeStatementNo as comparativeStatementNo, " +
            "cs.existingCsId as existingCsId " +
            "from PartsInvoice pi " +
            "inner join PartOrder po on po.id = pi.partOrderId " +
            "left join CsDetail csd on csd.id = po.csDetailId " +
            "inner join ComparativeStatement cs on cs.id = csd.comparativeStatementId " +
            "where pi.id = :id")
    ExistingCsProjection getComparativeStatement(@Param("id") Long partInvoiceId);

    ExistingCsProjection findComparativeStatementById(Long existingCsId);
}
