package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.planning.entity.Position;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OCCMSearchDto {
    @NotNull
    private Long aircraftId;

    private Boolean isPageable = false;

    private String description;
    private String partNumber;
    private String serialNumber;
    private LocalDate installationDate;
    private Double installationFH;
    private Integer installationFC;

}
