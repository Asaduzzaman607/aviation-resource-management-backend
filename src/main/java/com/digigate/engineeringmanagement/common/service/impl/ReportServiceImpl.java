package com.digigate.engineeringmanagement.common.service.impl;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.service.ReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

    /**
     * used for preparing report
     *
     * @param viewModel
     * @param fileType
     * @param fileName
     * @return
     */
    @Override
    public byte[] prepareReport(List<?> viewModel, String fileType, String fileName) {
        byte[] data;
        File file;
        try {
            file = ResourceUtils
                    .getFile(ApplicationConstant.JASPER_REPORT_BASE_PATH + fileName);
        } catch (FileNotFoundException ex) {
            LOGGER.error("File path not found. Exception: {}", ex);
            throw new EngineeringManagementServerException(
                    ErrorId.FILE_PATH_NOT_FOUND, HttpStatus.NOT_FOUND, MDC.get(ApplicationConstant.TRACE_ID)
            );
        }

        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(viewModel);
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("createdBy", "US-Bangla");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrBeanCollectionDataSource);
            if (fileType.equals("pdf")) {
                data = JasperExportManager.exportReportToPdf(jasperPrint);
            } else {
                ExporterInput input = new SimpleExporterInput(jasperPrint);
                try (ByteArrayOutputStream byteArray = new ByteArrayOutputStream()) {
                    OutputStreamExporterOutput output = new SimpleOutputStreamExporterOutput(byteArray);
                    JRXlsxExporter exporter = new JRXlsxExporter();
                    exporter.setExporterInput(input);
                    exporter.setExporterOutput(output);
                    exporter.exportReport();
                    data = byteArray.toByteArray();
                    output.close();
                } catch (IOException e) {
                    data = null;
                    e.printStackTrace();
                }
            }
        } catch (JRException ex) {
            LOGGER.error("Failed to load jasper template. Exception: {}", ex);
            throw new EngineeringManagementServerException(
                    ErrorId.FAILED_TO_JASPER_TEMPLATE, HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }

        if (Objects.isNull(data)) {
            LOGGER.error("Failed to load jasper template.");
            throw new EngineeringManagementServerException(
                    ErrorId.FAILED_TO_JASPER_TEMPLATE, HttpStatus.INTERNAL_SERVER_ERROR,
                    MDC.get(ApplicationConstant.TRACE_ID));
        }
        return data;
    }
}
