package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

/**
 * AircraftCheck for Aircraft ViewModel
 *
 * @author Ashraful
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftCheckForAircraftViewModel {

    Long acCheckId;

    String checkTitle;
}
