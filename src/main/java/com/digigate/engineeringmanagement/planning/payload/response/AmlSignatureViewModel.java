package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class AmlSignatureViewModel {
    private Long amlSignatureId;
    private Long signatureId;
    private Long employeeId;
    private String signatureName;
    private String authNo;
    private Integer signatureType;
    private LocalDateTime pfiDate;
    private LocalDateTime ocaDate;
    private Long airportId;
    private String airportName;
}
