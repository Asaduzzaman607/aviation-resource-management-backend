package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;

/**
 * Work order Component Response
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkOrderComponent {
    private String serialNo;
    private String station;
    private int qty = 1;
    private LocalDate removedDate;
    private LocalDate receivedDate;

    public WorkOrderComponent(String station, LocalDate removedDate) {
        this.station = station;
        this.removedDate = removedDate;
    }
}
