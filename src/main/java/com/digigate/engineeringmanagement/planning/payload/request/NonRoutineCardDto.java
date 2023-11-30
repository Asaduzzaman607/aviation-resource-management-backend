package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Non Routine Card Dto
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class NonRoutineCardDto implements IDto {
    @NotNull
    private Long aircraftId;
    private Long acCheckIndexId;
    @NotNull
    private String nrcNo;
    private String reference;
    @NotNull
    private LocalDate issueDate;
    private AMLDefectRectificationDto amlDefectRectificationDto;
}
