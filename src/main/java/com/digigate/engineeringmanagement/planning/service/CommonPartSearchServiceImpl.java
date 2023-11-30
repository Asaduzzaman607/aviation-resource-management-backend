package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.planning.dto.request.PartSearchDto;
import com.digigate.engineeringmanagement.planning.payload.request.CommonPartSearchDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CommonPartSearchServiceImpl implements CommonPartSearchService {

    private final PartService partService;

    public CommonPartSearchServiceImpl(PartService partService) {
        this.partService = partService;
    }

    /**
     * This method is responsible for common part search
     *
     * @param commonPartSearchDto  {@link CommonPartSearchDto}
     * @param pageable              page data
     * @return                      required result
     */
    @Override
    public PageData search(CommonPartSearchDto commonPartSearchDto, Pageable pageable) {
        PartSearchDto partSearchDto = PartSearchDto.builder()
                .partNo(commonPartSearchDto.getPartNo())
                .partClassification(commonPartSearchDto.getPartClassification())
                .isActive(commonPartSearchDto.getIsActive())
                .build();
        return partService.search(partSearchDto, pageable);
    }
}
