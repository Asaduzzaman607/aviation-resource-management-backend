package com.digigate.engineeringmanagement.configurationmanagement.controller.configuration;

import com.digigate.engineeringmanagement.common.controller.AbstractSearchController;
import com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration.ClientListRequestDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ClientList;
import com.digigate.engineeringmanagement.configurationmanagement.service.configuration.ClientListService;
import com.digigate.engineeringmanagement.storemanagement.payload.request.search.IdQuerySearchDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client-list")
public class ClientListController extends AbstractSearchController<ClientList, ClientListRequestDto, IdQuerySearchDto> {

    public ClientListController(ClientListService clientListService) {
        super(clientListService);
    }
}
