package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MappingDto{
    @NotNull
    private Long id;
    @NotNull
    private Long aircraftId;
}
