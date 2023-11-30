package com.digigate.engineeringmanagement.planning.payload.response;
import com.digigate.engineeringmanagement.planning.constant.AmlType;
import lombok.*;

import java.time.LocalDate;

/**
 * Oil Uplift report view model
 *
 * @author ashraful
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OilUpLiftReportViewModel {
    private Long amlId;
    private LocalDate date;
    private Integer pageNo;
    private Double airTime;
    private String fromAirport;
    private Long fromAirportId;
    private Double hydOil1;
    private Double hydOil2;
    private Double hydOil3;
    private Double engineOil1;
    private Double engineOil2;
    private Double apuOil;
    private Double upliftOilRecord;
    private Double onArrivalRecord;
    private Double totalOilRecord;
    private Double fuelConsumption;
    private Character alphabet;
    private AmlType amlType;

    public OilUpLiftReportViewModel(Long amlId, Long fromAirportId, LocalDate date, Integer pageNo,
                                    Character alphabet, AmlType amlType) {
        this.amlId = amlId;
        this.fromAirportId = fromAirportId;
        this.date = date;
        this.pageNo = pageNo;
        this.alphabet = alphabet;
        this.amlType = amlType;
    }
}
