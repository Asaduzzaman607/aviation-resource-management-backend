package com.digigate.engineeringmanagement.configurationmanagement.service.configuration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.configurationmanagement.dto.response.VendorWiseClientListResponseDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.ClientList;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Vendor;
import com.digigate.engineeringmanagement.configurationmanagement.entity.VendorWiseClientList;
import com.digigate.engineeringmanagement.configurationmanagement.repository.configuration.VendorWiseClientListRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VendorWiseClientServiceImpl implements VendorWiseClientService {
    private final ClientListService clientListService;
    private final VendorWiseClientListRepository vendorWiseClientListRepository;

    public VendorWiseClientServiceImpl(ClientListService clientListService, VendorWiseClientListRepository vendorWiseClientListRepository) {
        this.clientListService = clientListService;
        this.vendorWiseClientListRepository = vendorWiseClientListRepository;
    }

    @Override
    public void saveAll(Vendor vendor, List<Long> clientListIds) {
        Map<Long, ClientList> clientListMap = clientListService.findAllByClientListIdIn(clientListIds)
                .stream().collect(Collectors.toMap(ClientList::getId, Function.identity()));
        List<VendorWiseClientList> vendorWiseClientLists = clientListIds.stream()
                .map(clientIds -> convertToSaveEntity(vendor, clientListMap.get(clientIds)))
                .collect(Collectors.toList());
        try {
            vendorWiseClientListRepository.saveAll(vendorWiseClientLists);
        } catch (Exception e) {
            throw EngineeringManagementServerException.badRequest(ErrorId.DATA_NOT_SAVED);
        }
    }

    @Override
    public void updateAll(List<Long> clientListIds, Vendor vendor) {

        List<VendorWiseClientList> vendorWiseClientList = vendorWiseClientListRepository.findAllByVendorId(vendor.getId());
        List<VendorWiseClientList> finalVendorWiseClients = new ArrayList<>();

        if (Objects.nonNull(clientListIds)) {
            //save new clients
            Set<Long> clientIds = vendorWiseClientList.stream().map(VendorWiseClientList::getClientListId).filter(Objects::nonNull).collect(Collectors.toSet());
            List<Long> latestClientIds = clientListIds.stream().filter(newClientId -> !clientIds.contains(newClientId)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(latestClientIds)) {
                saveAll(vendor, latestClientIds);
            }
            //active old inactive vendorWiseClient
            List<VendorWiseClientList> activeOldClient = vendorWiseClientList.stream().filter(client -> clientListIds.contains(client.getClientList().getId())
                    && client.getIsActive().equals(Boolean.FALSE)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(activeOldClient)) {
                activeOldClient.forEach(client -> finalVendorWiseClients.add(convertReActiveEntity(client)));

            }
            //inActive active vendorWiseClient
            List<VendorWiseClientList> deletedClient = vendorWiseClientList.stream().filter(client -> !clientListIds.contains(client.getClientList().getId())
                    && client.getIsActive().equals(Boolean.TRUE)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deletedClient)) {
                deletedClient.forEach(client -> finalVendorWiseClients.add(convertDeleteEntity(client)));
            }


            vendorWiseClientListRepository.saveAll(finalVendorWiseClients);
        }
    }

    @Override
    public List<VendorWiseClientListResponseDto> getAllResponse(Set<Long> vendorIds) {
        List<VendorWiseClientList> vendorWiseClientLists = vendorWiseClientListRepository.findAllByVendorIdInAndIsActiveTrue(vendorIds);
        return vendorWiseClientLists.stream().map(this::ConvertToResponse).collect(Collectors.toList());
    }

    private  VendorWiseClientListResponseDto ConvertToResponse(VendorWiseClientList vendorWiseClientList) {
        return VendorWiseClientListResponseDto.builder()
                .id(vendorWiseClientList.getId())
                .clientName(vendorWiseClientList.getClientList().getClientName())
                .clientListId(vendorWiseClientList.getClientList().getId())
                .vendorId(vendorWiseClientList.getVendor().getId())
                .vendorName(vendorWiseClientList.getVendor().getName())
                .build();
    }

    private VendorWiseClientList convertDeleteEntity(VendorWiseClientList vendorWiseClientList) {
        vendorWiseClientList.setIsActive(false);
        return vendorWiseClientList;
    }

    private VendorWiseClientList convertReActiveEntity(VendorWiseClientList vendorWiseClientList) {
        vendorWiseClientList.setIsActive(true);
        return vendorWiseClientList;
    }

    private VendorWiseClientList convertToSaveEntity(Vendor vendor, ClientList clientList) {
        VendorWiseClientList vendorWiseClientList = new VendorWiseClientList();
        vendorWiseClientList.setVendor(vendor);
        vendorWiseClientList.setClientList(clientList);
        return vendorWiseClientList;
    }
}
