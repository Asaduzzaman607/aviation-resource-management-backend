package com.digigate.engineeringmanagement.planning.payload.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * Work Order Dto
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class WorkOrderDto implements IDto {

    @NotNull
    private Long aircraftId;
    private String workShopMaint;
    @NotBlank
    private String woNo;
    @NotNull
    private LocalDate date;
    @NotNull
    private Double totalAcHours;
    @NotNull
    private Integer totalAcLanding;
    private String tsnComp;
    private String tsoComp;
    @NotNull
    private LocalDate asOfDate;
    List<WoTaskDto> woTaskList;
}
