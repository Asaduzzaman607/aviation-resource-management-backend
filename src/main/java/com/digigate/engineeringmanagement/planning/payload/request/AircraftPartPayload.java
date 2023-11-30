package com.digigate.engineeringmanagement.planning.payload.request;


import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
/**
 * AircraftPartPayload
 *
 * @author Masud Rana
 */
@Getter
@Setter
@NoArgsConstructor
public class AircraftPartPayload implements IDto {
    private Long id;
    @NotNull
    private Long partId;
    @NotNull
    private Long higherPartId;
}
