package com.digigate.engineeringmanagement.storemanagement.payload.response.storedemand;

import lombok.Data;
import lombok.Value;

@Data
@Value(staticConstructor = "of")
public class IdVoucherDto {
    Long id;
    String voucher;
}
