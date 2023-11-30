package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DateWiseAircraftDelay
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DateWiseAircraftDelay {
    private Integer delayMonth;
    private Integer delayYear;
}
