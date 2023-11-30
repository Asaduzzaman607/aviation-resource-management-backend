package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.entity.AMLDefectRectification;
import com.digigate.engineeringmanagement.planning.payload.request.AMLDefectRectificationDto;
import com.digigate.engineeringmanagement.planning.payload.request.AmlDefectRectificationReportDto;
import com.digigate.engineeringmanagement.planning.payload.response.AmlDefectRectificationModelView;
import com.digigate.engineeringmanagement.planning.payload.response.AmlDefectRectificationReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.DefRectSearchViewModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AmlDefectRectificationService {

    List<AmlDefectRectificationModelView> getDefectRectificationsByAmlId(Long amlId);

    List<AmlDefectRectificationModelView> update(List<AMLDefectRectificationDto> defectRectificationDtos);

    List<AmlDefectRectificationModelView> create(List<AMLDefectRectificationDto> defectRectificationDtos);

    AMLDefectRectification findById(Long id);


    AMLDefectRectification findByIdUnfiltered(Long id);
    Page<AmlDefectRectificationReportViewModel> generateAmlDefectRectificationReport(
            AmlDefectRectificationReportDto amlDefectRectificationReportDto, Pageable pageable);
    AmlDefectRectificationModelView findDefectRectificationByNrcId(Long nrcId);

    void deleteDefectAndRectifications(List<Long> defectRectificationIds);

    List<DefRectSearchViewModel> searchDefectRectificationList(Long aircraftId, LocalDate fromDate, LocalDate toDate);
}
