package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.configurationmanagement.constant.CancellationTypeEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AcCancellations ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcCancellationsViewModel {

    private Long id;
    private String aircraftModelName;
    private Long aircraftModelId;
    private LocalDate date;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Integer cancellationTypeId;
    private CancellationTypeEnum cancellationTypeEnum;
}
