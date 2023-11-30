package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import lombok.Data;
import lombok.Value;

@Data
@Value(staticConstructor = "of")
public class PartOrderLiteDto {
    Long id;
    String orderNo;
}