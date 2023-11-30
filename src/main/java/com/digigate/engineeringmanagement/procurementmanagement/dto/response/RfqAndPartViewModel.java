package com.digigate.engineeringmanagement.procurementmanagement.dto.response;

import lombok.Value;

import java.util.List;

@Value(staticConstructor = "of")
public class RfqAndPartViewModel {
    RfqVendorResponseDto rfqVendorResponseDto;
    List<RfqPartViewModel> rfqPartViewModels;
}
