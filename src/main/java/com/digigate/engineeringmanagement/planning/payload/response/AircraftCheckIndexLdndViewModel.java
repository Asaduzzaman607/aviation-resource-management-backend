package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;
import org.jfree.util.Log;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * public class AircraftCheckIndexLdnd ViewModel
 *
 * @author Ashraful
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftCheckIndexLdndViewModel {

    private String taskCardReference;

    private String ampReference;

    private String taskDescription;

    private String partNo;

    private String serialNo;

    private Long serialId;

    private String inspType;

    private Set<String> trades;

    private LocalDate completionDate;
}
