package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ForecastGenerateDto {
    @Valid
    @NotEmpty
    List<ForecastRequest> forecastRequestList;
}
