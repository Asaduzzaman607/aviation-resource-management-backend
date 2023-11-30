package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.configurationmanagement.entity.Aircraft;
import lombok.*;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropellerDto implements IDto {
    private String nomenClature;
    private String partNo;
    private String serialNo;
    private LocalDate installationDate;
    private Long installationTsn;
    private Long installationTso;
    private Long currentTsn;
    private Long currentTso;
    private Integer limitMonth;
    private Integer limitFh;
    private LocalDate estimatedDate;
    private Long aircraftId;
    private Boolean isActive;
}
