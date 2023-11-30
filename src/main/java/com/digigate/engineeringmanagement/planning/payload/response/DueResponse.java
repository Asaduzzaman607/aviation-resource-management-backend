package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.DashboardItemType;
import com.digigate.engineeringmanagement.planning.constant.ItemColorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DueResponse {

    private Long id;
    private LocalDate estimatedDueDate;
    private LocalDate calenderDueDate;
    private DashboardItemType itemType;

    private Double nextDueHour;

    private Double remainingHour;

    private Long remainingDay;

    public DueResponse(Long id, LocalDate estimatedDueDate, LocalDate calenderDueDate, DashboardItemType itemType, Double nextDueHour) {
        this.id = id;
        this.estimatedDueDate = estimatedDueDate;
        this.calenderDueDate = calenderDueDate;
        this.itemType = itemType;
        this.nextDueHour = nextDueHour;
    }

    public DueResponse(Long id, LocalDate estimatedDueDate) {
        this.id = id;
        this.estimatedDueDate = estimatedDueDate;
    }
}
