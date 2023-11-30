package com.digigate.engineeringmanagement.storemanagement.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartSerialDto {
    private Long partId;
    private Long serialId;
}
