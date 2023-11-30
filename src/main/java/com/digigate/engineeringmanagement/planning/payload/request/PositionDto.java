package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PositionDto implements IDto {

    private Long positionId;
    @NotBlank
    private String name;
    private String description;
}
