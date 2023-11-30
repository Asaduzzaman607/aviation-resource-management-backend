package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AcPartSerialResponse {

    private Long abId;
    private Long partId;
    private String partNo;
    private Long serialId;
    private String serialNo;

    private Long positionId;
    private String position;

    public AcPartSerialResponse(Long abId, Long partId, String partNo, Long serialId, String serialNo) {
        this.abId = abId;
        this.partId = partId;
        this.partNo = partNo;
        this.serialId = serialId;
        this.serialNo = serialNo;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AcPartSerialResponse)) return false;
        return Objects.nonNull(this.getPartNo()) && Objects.nonNull(this.getSerialNo())
                && this.getPartNo().equals(((AcPartSerialResponse) object).getPartNo())
                && this.getSerialNo().equals(((AcPartSerialResponse) object).getSerialNo());
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getPartNo()) && Objects.isNull(this.getSerialNo())) {
            return this.getClass().hashCode();
        }
        return this.getPartNo().hashCode();
    }
}
