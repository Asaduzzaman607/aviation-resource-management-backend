package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;

/**
 * Wo Task ViewModel
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class WoTaskViewModel {
    private Long id;
    private Long workOrderId;
    private Integer slNo;
    private String description;
    private String workCardNo;
    private LocalDate complianceDate;
    private LocalDate accomplishDate;
    private String authNo;
    private String remarks;
}
