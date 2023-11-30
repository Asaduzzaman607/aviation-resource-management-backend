package com.digigate.engineeringmanagement.planning.repository;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.planning.entity.Propeller;
import com.digigate.engineeringmanagement.planning.payload.response.PropellerReportViewModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PropellerRepository
 *
 * @author Masud Rana
 */
@Repository
public interface PropellerRepository extends AbstractRepository<Propeller> {
}
