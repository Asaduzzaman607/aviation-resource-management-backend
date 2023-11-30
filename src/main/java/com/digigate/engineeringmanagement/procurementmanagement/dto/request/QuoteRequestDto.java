package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuoteRequestDto implements IDto {
    @NotNull
    private Long requisitionId;
}
