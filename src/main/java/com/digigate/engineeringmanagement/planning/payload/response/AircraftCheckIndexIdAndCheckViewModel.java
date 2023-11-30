package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.util.List;
import java.util.Set;

/**
 * Aircraft Check Index Id and Check titles view model
 *
 * @author ashinisingha
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AircraftCheckIndexIdAndCheckViewModel {
    private Long id;
    private List<String> titles;
}
