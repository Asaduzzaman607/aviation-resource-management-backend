package com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.constant.VendorWorkFlowType;
import com.digigate.engineeringmanagement.common.payload.IDto;
import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorDto implements IDto {
    private Long id;
    private VendorType vendorType;
    @NonNull
    private String name;
    private Long countryOriginId;
    private String address;
    private String officePhone;
    private String emergencyContact;
    @Email(regexp = ApplicationConstant.EMAIL_VALIDATION_REGEX, message = ErrorId.INVALID_EMAIL_PATTERN)
    private String email;
    private String website;
    private String skype;
    private List<Long> clientList;
    private String itemsBuild;
    private Set<String> attachments;
    private String loadingPort;
    private LocalDate validTill;
    private Boolean status = Boolean.FALSE;
    @NonNull
    private Long cityId;
    private String contactPerson;
    @Pattern(regexp = ApplicationConstant.PHONE_NUMBER_VALIDATION, message = ErrorId.NUMBER_INVALID)
    private String contactMobile;
    @Pattern(regexp = ApplicationConstant.PHONE_NUMBER_VALIDATION, message = ErrorId.NUMBER_INVALID)
    private String contactPhone;
    @Email(regexp = ApplicationConstant.EMAIL_VALIDATION_REGEX, message = ErrorId.INVALID_EMAIL_PATTERN)
    private String contactEmail;
    private String contactSkype;
    private List<VendorCapabilityLogRequestDto> vendorCapabilityLogRequestDtoList = new ArrayList<>();
}
