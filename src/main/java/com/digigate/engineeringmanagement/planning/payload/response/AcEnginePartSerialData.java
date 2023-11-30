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
public class AcEnginePartSerialData {
    private Long partId;
    private String serialNo;


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AcEnginePartSerialData)) return false;
        return Objects.nonNull(this.getSerialNo()) && this.getSerialNo().equals(((AcEnginePartSerialData) object).getSerialNo());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getSerialNo())) {
            return this.getClass().hashCode();
        }
        return this.getSerialNo().hashCode();
    }
}
