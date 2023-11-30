package com.digigate.engineeringmanagement.common.service;

import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardCommonDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardProjection;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardResponseDto;
import com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive.DashboardViewProjection;
import com.digigate.engineeringmanagement.storemanagement.service.scrap.StoreScrapPartService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {
    private final StorePartAvailabilityService storePartAvailabilityService;
    private final StoreScrapPartService storeScrapPartService;
    private final StoreReturnPartService storeReturnPartService;
    private final StoreDemandService storeDemandService;
    private final StoreIssueService storeIssueService;
    private final ProcurementRequisitionService procurementRequisitionService;

    public DashboardService(StorePartAvailabilityService storePartAvailabilityService,
                            StoreScrapPartService storeScrapPartService,
                            StoreReturnPartService storeReturnPartService,
                            StoreDemandService storeDemandService,
                            StoreIssueService storeIssueService,
                            ProcurementRequisitionService procurementRequisitionService) {

        this.storePartAvailabilityService = storePartAvailabilityService;
        this.storeScrapPartService = storeScrapPartService;
        this.storeReturnPartService = storeReturnPartService;
        this.storeDemandService = storeDemandService;
        this.storeIssueService = storeIssueService;
        this.procurementRequisitionService = procurementRequisitionService;
    }

    public List<DashboardViewProjection> findPartInfoAndIsActiveTrue(LocalDate startDate, LocalDate endDate) {
        return storePartAvailabilityService.findPartInfoAndIsActiveTrue(startDate, endDate);
    }

    public DashboardResponseDto getDashboardData(Integer month) {
        DashboardResponseDto dashboardResponseDto = new DashboardResponseDto();
        dashboardResponseDto.setStoreDemandData(getStoreDemandData(month));
        dashboardResponseDto.setStoreIssueData(getStoreIssueData(month));
        dashboardResponseDto.setProcurementRequisitionData(getProcurementRequisitionData(month));
        dashboardResponseDto.setReturnPartInfo(getPartStatusInfo((month/-month)));
        dashboardResponseDto.setScrapPartInfo(getPartInfo((month/-month)));
        return dashboardResponseDto;
    }


    public List<DashboardCommonDto> getStoreDemandData(Integer month) {
        List<DashboardProjection> dashboardProjectionList = storeDemandService.getStoreDemandData(month);
        List<DashboardCommonDto> dashboardCommonDtoList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(dashboardProjectionList)) {
            dashboardProjectionList.forEach(demand -> {
                setCommonData(demand, dashboardCommonDtoList);
            });
        }
        return dashboardCommonDtoList;
    }

    public void setCommonData(DashboardProjection dashboardProjection, List<DashboardCommonDto> dashboardCommonDtoList) {
        DashboardCommonDto dashboardCommonDto = new DashboardCommonDto();
        dashboardCommonDto.setCount(dashboardProjection.getTotal());
        dashboardCommonDto.setMonth(dashboardProjection.getMnth());
        dashboardCommonDto.setYear(dashboardProjection.getYr());
        dashboardCommonDto.setPartStatus(dashboardProjection.getPartStatus());
        dashboardCommonDto.setPartClassification(dashboardProjection.getPartClassification());
        dashboardCommonDtoList.add(dashboardCommonDto);
    }


    public List<DashboardCommonDto> getStoreIssueData(Integer month) {
        List<DashboardProjection> dashboardProjectionList = storeIssueService.getStoreIssueData(month);
        List<DashboardCommonDto> dashboardCommonDtoList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(dashboardProjectionList)) {
            dashboardProjectionList.forEach(issue -> {
                setCommonData(issue, dashboardCommonDtoList);
            });
        }
        return dashboardCommonDtoList;
    }

    public List<DashboardCommonDto> getProcurementRequisitionData(Integer month) {
        List<DashboardProjection> dashboardProjectionList = procurementRequisitionService.getProcurementRequisitionData(month);
        List<DashboardCommonDto> dashboardCommonDtoList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(dashboardProjectionList)) {
            dashboardProjectionList.forEach(procurement -> {
                setCommonData(procurement, dashboardCommonDtoList);
            });
        }
        return dashboardCommonDtoList;
    }

    public List<DashboardCommonDto> getPartStatusInfo(Integer month) {
        List<DashboardProjection> dashboardProjectionList = storeReturnPartService.getStoreReturnPartDataForLastOneMonth(month);
        List<DashboardCommonDto> dashboardCommonDtoList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(dashboardProjectionList)) {
            dashboardProjectionList.forEach(partStatusInfo -> {
                setCommonData(partStatusInfo, dashboardCommonDtoList);
            });
        }
        return dashboardCommonDtoList;
    }

    public List<DashboardCommonDto> getPartInfo(Integer month) {
        List<DashboardProjection> dashboardProjectionList = storeScrapPartService.getPartInfoForLastOneMonth(month);
        List<DashboardCommonDto> dashboardCommonDtoList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(dashboardProjectionList)) {
            dashboardProjectionList.forEach(part -> {
                setCommonData(part, dashboardCommonDtoList);
            });
        }
        return dashboardCommonDtoList;
    }
}
