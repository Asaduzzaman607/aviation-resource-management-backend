package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cancellation And Sd ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CancellationAndSdViewModel {
    private Integer month;
    private Integer initialCancellationCount;
    private Integer scheduledDep;
}
