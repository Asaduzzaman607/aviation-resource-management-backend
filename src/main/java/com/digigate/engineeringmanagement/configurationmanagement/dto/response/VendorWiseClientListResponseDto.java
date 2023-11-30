package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VendorWiseClientListResponseDto {
    private Long id;
    private Long vendorId;
    private String vendorName;
    private Long clientListId;
    private String clientName;
}
