package com.digigate.engineeringmanagement.storemanagement.repository.storeconfiguration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.procurementmanagement.dto.projection.CurrencyProjection;
import com.digigate.engineeringmanagement.storemanagement.entity.storeconfiguration.Currency;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface CurrencyRepository extends AbstractRepository<Currency> {

    List<Currency> findByCode(String code);

    Collection<CurrencyProjection> findCurrencyByIdIn(Set<Long> collectionsOfCurrencyIds);
}
