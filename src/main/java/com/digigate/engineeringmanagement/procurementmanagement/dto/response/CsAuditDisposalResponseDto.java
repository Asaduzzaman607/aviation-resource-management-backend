package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsAuditDisposalResponseDto {
    private Long id;
    private String auditDisposal;
    private Long submittedById;
    private String submittedByName;
    private Set<String> attachments;
}
