package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Alert Level By Location ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertLevelByLocation {
    private Long locationId;
    private String locationName;
    private Double alertLevel;
    private String systemName;
}
