package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Work Order View Model
 *
 * @author ashinisingha
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class WorkOrderViewModel {
    private Long id;
    private Long aircraftId;
    private String aircraftName;
    private String workShopMaint;
    private String woNo;
    private LocalDate date;
    private Double totalAcHours;
    private Integer totalAcLanding;
    private String tsnComp;
    private String tsoComp;
    private String airframeSerial;
    private LocalDate asOfDate;
    private List<WoTaskViewModel> woTaskViewModelList;
}
