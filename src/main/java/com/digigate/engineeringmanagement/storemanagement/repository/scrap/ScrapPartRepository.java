package com.digigate.engineeringmanagement.storemanagement.repository.scrap;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.storemanagement.entity.scrap.StoreScrapPart;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ScrapPartRepository extends AbstractRepository<StoreScrapPart> {
    boolean existsByStoreScrapIdAndIsActiveTrue(Long id);

    List<StoreScrapPart> findByStoreScrapIdInAndIsActiveTrue(Set<Long> ids);

    @Query(value = "SELECT top 2 COUNT(ssp.id) as total,p.classification as partClassification, MONTH(GETDATE()) as mnth\n" +
            "FROM store_scrap_parts as ssp inner join parts as p on\n" +
            "ssp.part_id = p.id\n" +
            "where ssp.created_at >= DATEADD(M, :month, GETDATE())\n" +
            "GROUP BY p.classification", nativeQuery = true)
    List<DashboardProjection> getPartInfoForLastOneMonth(@Param("month") Integer month);
}
