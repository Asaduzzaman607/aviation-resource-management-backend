package com.digigate.engineeringmanagement.storemanagement.payload.request.storeconfiguration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RackDto implements IDto {
    private Long rackId;
    @NotBlank
    @Size(min = 1, max = 100)
    private String rackCode;
    @Digits(integer = 15, fraction = 5)
    private Double rackHeight;
    @Digits(integer = 15, fraction = 5)
    private Double rackWidth;
    @NotNull(message = ErrorId.ID_IS_REQUIRED)
    private Long roomId;
}
