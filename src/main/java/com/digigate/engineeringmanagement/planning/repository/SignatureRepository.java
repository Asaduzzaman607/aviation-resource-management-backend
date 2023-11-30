package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.Signature;
import com.digigate.engineeringmanagement.planning.payload.response.SignatureViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Signature repository
 *
 * @author ashinisingha
 */
@Repository
public interface SignatureRepository extends AbstractRepository<Signature> {
    Boolean existsByAuthNo(String authNo);

    @Query("select sg from Signature sg join Employee em on sg.employeeId = em.id where sg.isActive = true " +
            "and em.isActive = true")
    List<Signature> getAllByIsActiveTrue();

    @Query(" SELECT new com.digigate.engineeringmanagement.planning.payload.response.SignatureViewModel(" +
            " sg.id, sg.employee.id, sg.employee.name, sg.authNo, sg.isActive ) " +
            " from Signature sg WHERE " +
            " (:employeeName is null  or sg.employee.name LIKE :employeeName% ) " +
            " AND (:authNo is null or sg.authNo LIKE :authNo% ) " +
            " AND (:isActive is null or sg.isActive = :isActive ) ")

    Page<SignatureViewModel>findSignatureBySearchCriteria(
        @Param("employeeName") String employeeName,
        @Param("authNo") String authNo,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );
}
