package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.repository.AbstractRepository;
import com.digigate.engineeringmanagement.common.service.AbstractSearchService;
import com.digigate.engineeringmanagement.common.specification.CustomSpecification;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.ClientListRequestDto;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.ClientListResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ClientList;
import com.digigate.engineeringmanagement.configurationmanagement.repository.configuration.ClientListRepository;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ClientListService extends AbstractSearchService<ClientList, ClientListRequestDto, IdQuerySearchDto> {
    private final ClientListRepository clientListRepository;

    public ClientListService(AbstractRepository<ClientList> repository, ClientListRepository clientListRepository) {
        super(repository);
        this.clientListRepository = clientListRepository;
    }

    public List<ClientList> findAllByClientListIdIn(List<Long> ids) {
        return clientListRepository.findClientListByIdIn(ids);
    }

    @Override
    protected Specification<ClientList> buildSpecification(IdQuerySearchDto searchDto) {
        CustomSpecification<ClientList> customSpecification = new CustomSpecification<>();
        return Specification.where(customSpecification.likeSpecificationAtRoot(searchDto.getQuery(), ApplicationConstant.CLIENT_NAME));
    }

    @Override
    protected ClientListResponseDto convertToResponseDto(ClientList clientList) {
        return ClientListResponseDto.builder()
                .id(clientList.getId())
                .clientName(clientList.getClientName())
                .build();
    }

    @Override
    protected ClientList convertToEntity(ClientListRequestDto clientListRequestDto) {
        return prepareEntity(clientListRequestDto, new ClientList());
    }


    @Override
    protected ClientList updateEntity(ClientListRequestDto dto, ClientList entity) {
        return prepareEntity(dto, entity);
    }

    private ClientList prepareEntity(ClientListRequestDto clientListRequestDto, ClientList clientList) {
        validClient(clientListRequestDto, clientList);
        clientList.setClientName(clientListRequestDto.getClientName());
        return clientList;
    }

    private void validClient(ClientListRequestDto dto, ClientList old) {
        List<ClientList> clientLists = clientListRepository.findByClientName(dto.getClientName());
        if (!CollectionUtils.isEmpty(clientLists)) {
            clientLists.forEach(client -> {
                if (Objects.nonNull(old) && client.equals(old)) {
                    return;
                }
                throw EngineeringManagementServerException.badRequest(ErrorId.CLIENT_NAME_ALREADY_EXIST);
            });
        }
    }
}
