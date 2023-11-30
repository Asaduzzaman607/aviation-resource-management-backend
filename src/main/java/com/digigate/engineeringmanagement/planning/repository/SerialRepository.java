package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.Serial;
import com.digigate.engineeringmanagement.planning.payload.response.SerialListViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.SerialResponseView;
import com.digigate.engineeringmanagement.planning.payload.response.SerialViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SerialRepository extends AbstractRepository<Serial> {
    Optional<Serial> findByPartIdAndSerialNumber(Long partId, String serialNumber);

    @Query(" select new com.digigate.engineeringmanagement.planning.payload.response.SerialResponseView(" +
            "s.id," +
            "s.serialNumber" +
            ")" +
            "from Serial s where s.partId=:partId and s.isActive=true")
    List<SerialResponseView> findAllByPartId(Long partId);

    Set<Serial> findAllByPartIdInAndIsActiveTrue(Collection<Long> partId);

    @Query(" select new com.digigate.engineeringmanagement.planning.payload.response.SerialViewModel(" +
            "s.id," +
            "s.partId," +
            "s.serialNumber," +
            "p.partNo," +
            "s.isActive" +
            ") " +
            "from Serial s " +
            "join Part p on s.partId = p.id " +
            "where " +
            "(:serialNumber is null or s.serialNumber LIKE :serialNumber% " +
            " or (replace(s.serialNumber,'-','') LIKE :serialNumber%)  ) " +
            "and (:partId is null or s.partId = :partId) " +
            "and (:partNo is null or p.partNo like %:partNo%)" +
            "and (s.isActive = :isActive)")
    Page<SerialViewModel> findSerial(String serialNumber, Long partId, String partNo, Boolean isActive, Pageable pageable);

    Optional<Serial> findByIdAndPartIdAndIsActiveTrue(Long serialId, Long partId);

    @Query(value = "SELECT new com.digigate.engineeringmanagement.planning.payload.response.SerialListViewModel(" +
            "p.partNo, " +
            "s.serialNumber" +
            ")" +
            "FROM Serial s " +
            "Join Part p on p.id = s.partId " +
            "and s.isActive = true " +
            "and p.isActive = true " +
            "ORDER BY s.id")
    List<SerialListViewModel> findAllSerialByList();

}
