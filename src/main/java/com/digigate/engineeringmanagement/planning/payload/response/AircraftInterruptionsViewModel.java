package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Aircraft Interruptions View Model
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AircraftInterruptionsViewModel {
    private Long id;
    private Long aircraftId;
    private String aircraftName;
    private Long locationId;
    private String locationName;
    private LocalDate date;
    private String defectDescription;
    private String rectDescription;
    private Double duration;
    private String amlPageNo;
    private String seqNo;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
