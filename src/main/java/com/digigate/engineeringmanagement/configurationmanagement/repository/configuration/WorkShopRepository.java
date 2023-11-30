package com.digigate.engineeringmanagement.configurationmanagement.repository.configuration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.entity.WorkShop;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkShopRepository extends AbstractRepository<WorkShop> {

    List<WorkShop> findByCodeIgnoreCase(String code);
}
