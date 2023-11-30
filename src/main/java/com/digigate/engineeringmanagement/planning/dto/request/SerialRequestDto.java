package com.digigate.engineeringmanagement.planning.dto.request;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerialRequestDto implements IDto {
    @NotNull(message = ErrorId.PART_ID_IS_REQUIRED)
    private Long partId;
    @NotBlank
    private String serialNumber;

}
