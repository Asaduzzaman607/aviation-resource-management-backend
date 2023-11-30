package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;

/**
 * Non Routine Card View Model
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class NonRoutineCardViewModel {
    private Long nonRoutineCardId;
    private Long acCheckIndexId;
    private String aircraftChecksName;
    private Long aircraftId;
    private String aircraftName;
    private String nrcNo;
    private String reference;
    private LocalDate issueDate;
    private Boolean isActive;
    private AmlDefectRectificationModelView amlDefectRectificationModelView;
}
