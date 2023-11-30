package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.configurationmanagement.constant.CancellationTypeEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AcCancellations Report ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AcCancellationsReportViewModel {

    private Long id;
    private CancellationTypeEnum cancellationTypeEnum;
    private LocalDate date;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
