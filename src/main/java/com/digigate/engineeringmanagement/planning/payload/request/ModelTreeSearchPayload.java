package com.digigate.engineeringmanagement.planning.payload.request;


import com.digigate.engineeringmanagement.common.payload.SDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ModelTreeSearchPayload implements SDto {
    private Long locationId;
    private Long modelId;
    private Long higherModelId;
    private Long positionId;
    private Boolean isActive;
}
