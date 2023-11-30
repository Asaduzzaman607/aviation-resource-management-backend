package com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OfficeDto implements IDto {
    private Long id;
    @NotBlank
    @Size(min = 1, max = 100)
    private String code;
    @Size(max = 8000)
    private String address;
    @NotNull(message = ErrorId.ID_IS_REQUIRED)
    private Long locationId;
    private String locationCode;
}
