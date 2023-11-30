package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.*;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManHourDto {
    @NotNull
    private Long acCheckIndexId;
    private Boolean isPageable = false;
}
