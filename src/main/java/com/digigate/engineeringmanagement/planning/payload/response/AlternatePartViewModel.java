package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

/**
 * Part view model
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AlternatePartViewModel {
    private Long id;
    private String partNo;
}
