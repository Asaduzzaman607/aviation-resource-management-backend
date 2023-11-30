package com.digigate.engineeringmanagement.planning.payload.request;


import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
@NoArgsConstructor
public class ModelTreePayload implements IDto {
    @NotNull
    private Long locationId;
    @NotNull
    private Long modelId;
    @NotNull
    private Long higherModelId;
    private Long positionId;
}
