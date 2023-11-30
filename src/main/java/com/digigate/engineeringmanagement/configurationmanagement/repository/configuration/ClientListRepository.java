package com.digigate.engineeringmanagement.configurationmanagement.repository.configuration;

import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ClientList;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientListRepository extends AbstractRepository<ClientList> {
    List<ClientList> findByClientName(String ClientName);

    List<ClientList> findClientListByIdIn(List<Long> ids);
}
