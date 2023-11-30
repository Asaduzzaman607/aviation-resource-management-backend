package com.digigate.engineeringmanagement.storemanagement.payload.request.partsreceive;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class StoreStockInwardDto implements IDto {

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveDate;
    private Integer tptMode;
    private String flightNo;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalDate;
    private String airwaysBill;
    private String invoiceNo;
    private String packingMode;
    private String packingNo;
    private Integer weight;
    private Integer noOfItems;
    private String description;
    private String importNo;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime importDate;
    private String discrepancyReportNo;
    private String remarks;
    private Long receivedBy;
    private Set<String> attachments;
}
