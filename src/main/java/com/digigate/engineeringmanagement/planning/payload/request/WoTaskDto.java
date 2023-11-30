package com.digigate.engineeringmanagement.planning.payload.request;

import lombok.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Wo Task Dto
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class WoTaskDto {
    private Long woTaskId;
    private Long workOrderId;
    @NotNull
    private Integer slNo;
    private String description;
    private String workCardNo;
    private LocalDate complianceDate;
    private LocalDate accomplishDate;
    private String authNo;
    private String remarks;
}
