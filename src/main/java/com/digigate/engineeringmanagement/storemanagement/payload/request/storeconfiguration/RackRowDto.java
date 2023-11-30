package com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RackRowDto implements IDto {
    private Long rackRowId;
    @NotBlank
    @Size(min = 1,max = 100)
    private String rackRowCode;
    @NotNull(message = ErrorId.ID_IS_REQUIRED)
    private Long rackId;
}
