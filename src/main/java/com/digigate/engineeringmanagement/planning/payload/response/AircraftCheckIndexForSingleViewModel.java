package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * AircraftCheckIndexForSingleView ViewModel
 *
 * @author Ashraful
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftCheckIndexForSingleViewModel {

    private Long id;

    private String woNo;

    private LocalDate woDate;

    private LocalDate asOfDate;

    private String acRegn;

    private String acMsn;

    private Double acHours;

    private Integer acCycles;

    private List<String> acCheckIndexNames;

    private List<AircraftCheckIndexLdndViewModel> aircraftCheckIndexLdnds;
}
