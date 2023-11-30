package com.digigate.engineeringmanagement.configurationmanagement.dto.request.administration;

import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkFLowActionRequestDto implements IDto {
    @NotBlank
    private String name;
    @NotNull(message = ErrorId.ACTION_FLOW_ORDER_NUMBER_IS_NULL)
    @Min(1)
    private Integer orderNumber;
    private String label;
}
