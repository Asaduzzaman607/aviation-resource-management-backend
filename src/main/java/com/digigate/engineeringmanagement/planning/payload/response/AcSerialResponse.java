package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AcSerialResponse {
    private String partNo;
    private Long serialId;
    private String serialNo;


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AcSerialResponse)) return false;
        return Objects.nonNull(this.getSerialNo()) && this.getSerialNo().equals(((AcSerialResponse) object).getSerialNo());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getSerialNo())) {
            return this.getClass().hashCode();
        }
        return this.getPartNo().hashCode();
    }
}
