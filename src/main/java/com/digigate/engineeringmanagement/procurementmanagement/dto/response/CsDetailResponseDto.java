package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CsDetailResponseDto {
    private Long id;
    private Long quoteId;
    private String quoteNo;
}
