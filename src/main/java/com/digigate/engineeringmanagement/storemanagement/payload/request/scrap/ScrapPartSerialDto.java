package com.digigate.engineeringmanagement.storemanagement.payload.request.scrap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScrapPartSerialDto {

    private Integer quantity = 1;
    private Long storeSerialId;
    private Boolean isActive;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ScrapPartSerialDto)) return false;
        return Objects.nonNull(this.getStoreSerialId()) && Objects.equals(this.getStoreSerialId(), (((ScrapPartSerialDto) object).getStoreSerialId()));
    }

    @Override
    public int hashCode() {
        if (Objects.isNull(this.getStoreSerialId())) {
            return this.getClass().hashCode();
        }
        return this.getStoreSerialId().hashCode();
    }
}
