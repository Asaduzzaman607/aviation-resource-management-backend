package com.digigate.engineeringmanagement.storemanagement.payload.response.partsreceive;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
public class StoreStockInwardViewModel {

    private Long id;
    private String serialNo;
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
    private String receiverName;
    private Long orderId;
    private String orderNo;
    private Set<String> attachments;
}
