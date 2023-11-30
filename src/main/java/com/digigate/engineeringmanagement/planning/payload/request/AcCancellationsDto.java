package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.configurationmanagement.constant.CancellationTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * AcCancellations Dti
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AcCancellationsDto implements IDto {

    @NotNull
    private Long aircraftModelId;

    @NotNull
    private LocalDate date;

    @NotNull
    private CancellationTypeEnum cancellationTypeEnum;
}
