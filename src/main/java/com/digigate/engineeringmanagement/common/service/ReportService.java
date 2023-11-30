package com.digigate.engineeringmanagement.common.service;

import java.util.List;

public interface ReportService {
    byte[] prepareReport(List<?> viewModel, String fileType, String fileName);
}
