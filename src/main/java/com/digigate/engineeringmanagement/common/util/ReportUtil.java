package com.digigate.engineeringmanagement.common.util;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class ReportUtil {
    public static String prepareContentType(String fileType) {
        String contentType = "application/pdf";
        if (fileType.equals("xlsx")) {
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }

        return contentType;
    }

    public static HttpHeaders prepareHttpHeader(String fileType, String fileName) {
        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename(fileName + "." + fileType ).build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentDisposition(contentDisposition);
        return httpHeaders;
    }
}
