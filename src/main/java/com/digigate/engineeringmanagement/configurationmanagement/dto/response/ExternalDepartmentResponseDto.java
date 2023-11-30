package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExternalDepartmentResponseDto {
    private Long id;
    private String address;
    private String officePhone;
    private Long countryOriginId;
    private String contactPerson;
    private String contactSkype;
    private String website;
    private Long cityId;
    private String cityName;
    private Long countryId;
    private String countryName;
    private VendorType vendorType;
    private String name;
    private String emergencyContact;
    private String email;
    private String skype;
    private String clientList;
    private String itemsBuild;
    private String loadingPort;
    private LocalDate validTill;
    private Boolean status;
    private Set<String> attachments;
}
