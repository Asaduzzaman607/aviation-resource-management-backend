package com.digigate.engineeringmanagement.planning.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * DTO to return inactive aircraft build part to store
 *
 * @author Junaid Khan Pathan
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcBuildPartReturnDto {
    private Long aircraftId;
    private Long partId;
    private Long serialId;
    private Long positionId;
    private Double tsn;
    private Integer csn;
    private Double tsr;
    private Integer csr;
    private Double tso;
    private Integer cso;
    private String removalReason;
    private LocalDate removalDate;
    private Boolean isInactive;
    private String authNo;
    private String sign;
    private LocalDate createdDate;
}
