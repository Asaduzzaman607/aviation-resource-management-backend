package com.digigate.engineeringmanagement.storemanagement.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OfficeIdNameDto {
    private Long officeId;
    private String officeCode;
}
