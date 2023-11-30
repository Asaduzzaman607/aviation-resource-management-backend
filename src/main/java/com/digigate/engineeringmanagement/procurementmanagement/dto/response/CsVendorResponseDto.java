package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CsVendorResponseDto {
    private Long id;
    private Long csDetailId;
    private Long vendorId;
    private VendorType vendorType;
    private String vendorName;
    private String vendorWorkFlowName;
    private LocalDate validTill;
    private List<CsqDetailResponseDto> csqDetailResponseDtoList;
}
