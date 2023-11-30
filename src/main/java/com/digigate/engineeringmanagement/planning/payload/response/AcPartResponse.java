package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AcPartResponse {
    private Long partId;
    private String partNo;


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AcPartResponse)) return false;
        return Objects.nonNull(this.getPartNo()) && this.getPartNo().equals(((AcPartResponse) object).getPartNo());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getPartNo())) {
            return this.getClass().hashCode();
        }
        return this.getPartNo().hashCode();
    }
}
