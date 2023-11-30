package com.digigate.engineeringmanagement.storemanagement.repository.storedemand;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.partsdemand.StoreIssue;
import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreIssueProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StoreIssueRepository extends AbstractRepository<StoreIssue> {

    Set<StoreIssueProjection> findByIdIn(Set<Long> ids);
    Page<StoreIssue> findAllByIsActiveAndVoucherNoContains
            (Boolean isActive, String query, Pageable pageable);
    Page<StoreIssue> findAllByIsActiveAndWorkFlowActionIdAndVoucherNoContains
            (Boolean isActive, Long approvedId, String query, Pageable pageable);

    boolean existsByStoreDemandIdAndIsActiveTrue(Long demandId);

    Optional<StoreIssue> findByStoreDemandIdAndWorkFlowActionId(Long demandId, Long id);
    List<StoreIssueProjection> findByStoreDemandIdInAndIsActiveTrue(Set<Long> demandIds);
    Page<StoreIssue> findAllByIsRejectedTrueAndVoucherNoContains
            (String query, Pageable pageable);
    Page<StoreIssue>findAllByIsRejectedFalseAndIsActiveAndWorkFlowActionIdInAndVoucherNoContains
            (Boolean isActive, Set<Long> workflowIds, String query, Pageable pageable);

    @Query(value = "SELECT COUNT(id) AS total, yr, mnth\n" +
            "FROM (\n" +
            "    SELECT id, YEAR(created_at) AS yr, MONTH(created_at) AS mnth\n" +
            "    FROM store_issues ss\n" +
            "    WHERE ss.voucher_no NOT LIKE 'INVISIBLE%' \n" +
            "      AND ss.created_at >= DATEADD(MONTH, :month, GETDATE())\n" +
            ") AS subquery\n" +
            "GROUP BY yr, mnth\n" +
            "ORDER BY yr , mnth ", nativeQuery = true)
    List<DashboardProjection> getStoreIssueDataForMonths(@Param("month") Integer month);
}
