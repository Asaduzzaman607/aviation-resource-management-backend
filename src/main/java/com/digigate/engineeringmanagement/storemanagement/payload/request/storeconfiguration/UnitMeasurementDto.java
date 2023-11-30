package com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UnitMeasurementDto implements IDto {
    @NotBlank
    @Size(max = 100)
    private String code;
}
