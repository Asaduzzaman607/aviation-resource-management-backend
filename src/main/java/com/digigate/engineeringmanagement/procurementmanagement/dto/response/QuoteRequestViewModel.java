package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.procurementmanagement.constant.InputType;
import com.digigate.engineeringmanagement.procurementmanagement.constant.RfqType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class QuoteRequestViewModel {
    private Long id;
    private String rfqNo;
    private LocalDateTime rfqDate;
    private Long requisitionId;
    private Long partOrderId;
    private String voucherNo;
    private String orderNo;
    private RfqType rfqType;
    private InputType inputType;
    private Boolean isRejected;
    private String rejectedDesc;
}
