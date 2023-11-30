package com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationDto implements IDto {
    private Long id;
    @NotBlank
    @Size(max = 100)
    private String code;
    @NotBlank
    @Size(max = 8000)
    private String address;
    @NotNull(message = ErrorId.CITY_ID_REQUIRED)
    private Long cityId;
}
