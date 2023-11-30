package com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand;

import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreIssueSerialProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrnAndSerialDto {
    private String grnNo;
    private Integer quantity = 1;
    private Long serialId;
    private Long issueItemId;
    private String serialNo;
    private Double price;

    public static GrnAndSerialDto from(StoreIssueSerialProjection storeIssueSerialProjection) {

        return new GrnAndSerialDto(storeIssueSerialProjection.getGrnNo(),
                storeIssueSerialProjection.getQuantity(),
                storeIssueSerialProjection.getStorePartSerialId(),
                storeIssueSerialProjection.getStoreIssueItemId(),
                storeIssueSerialProjection.getStorePartSerialSerialSerialNumber(),
                storeIssueSerialProjection.getStorePartSerialPrice()
        );

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrnAndSerialDto that = (GrnAndSerialDto) o;
        return Objects.equals(getSerialId(), that.getSerialId())
                && Objects.equals(getIssueItemId(), that.getIssueItemId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSerialId(), getIssueItemId());
    }
}
