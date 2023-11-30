package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

/**
 * AML Book View model
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AmlBookViewModel {
    private Long id;
    private Long aircraftId;
    private String aircraftName;
    private String bookNo;
    private Integer startPageNo;
    private Integer endPageNo;
    private Boolean isActive;
}
