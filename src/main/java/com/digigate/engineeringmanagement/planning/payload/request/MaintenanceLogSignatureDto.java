package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaintenanceLogSignatureDto {
    private Long amlSignatureId;
    private Long signatureId;
    private Integer signatureType;
}
