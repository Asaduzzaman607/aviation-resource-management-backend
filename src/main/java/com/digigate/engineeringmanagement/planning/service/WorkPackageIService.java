package com.digigate.engineeringmanagement.planning.service;

import com.digigate.engineeringmanagement.planning.entity.WorkPackage;
import com.digigate.engineeringmanagement.planning.payload.request.WorkPackageDto;
import com.digigate.engineeringmanagement.planning.payload.response.WorkPackageCertificateReportViewModel;
import com.digigate.engineeringmanagement.planning.payload.response.WorkPackageSummaryReportViewModel;

public interface WorkPackageIService {
    WorkPackageSummaryReportViewModel getReport(Long workPackageId);
    WorkPackageCertificateReportViewModel getCertificateReport(Long workPackageId);

    WorkPackage saveCertification(WorkPackageDto dto);

    WorkPackage updateCertification(WorkPackageDto dto, Long id);
}
