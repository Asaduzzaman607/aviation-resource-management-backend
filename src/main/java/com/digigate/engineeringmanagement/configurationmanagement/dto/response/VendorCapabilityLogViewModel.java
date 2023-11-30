package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VendorCapabilityLogViewModel {
    private Long parentId;
    private String parentName;
    private VendorType vendorType;
    List<VendorCapabilityViewModel> logStatus;
}
