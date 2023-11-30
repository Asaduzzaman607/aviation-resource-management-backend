package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MonthData {
    private Integer month;
    private Integer year;

    private Boolean addAsZero = false;

    public MonthData(Integer month, Integer year) {
        this.month = month;
        this.year = year;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MonthData)) return false;
        return Objects.nonNull(this.getMonth()) && Objects.nonNull(this.getYear())
                && this.getYear().equals(((MonthData) object).getYear())
                && this.getMonth().equals(((MonthData) object).getMonth());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getMonth())) {
            return this.getClass().hashCode();
        }
        return this.getMonth().hashCode();
    }
}
