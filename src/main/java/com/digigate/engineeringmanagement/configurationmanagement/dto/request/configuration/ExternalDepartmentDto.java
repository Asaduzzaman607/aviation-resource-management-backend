package com.digigate.engineeringmanagement.configurationmanagement.dto.request.configuration;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class ExternalDepartmentDto implements IDto {
    private Long id;
    @NonNull
    private String name;
    private Long countryOriginId;
    private String address;
    @Pattern(regexp = ApplicationConstant.PHONE_NUMBER_VALIDATION, message = ErrorId.NUMBER_INVALID)
    private String officePhone;
    private String emergencyContact;
    @Email(regexp = ApplicationConstant.EMAIL_VALIDATION_REGEX, message = ErrorId.INVALID_EMAIL_PATTERN)
    private String email;
    private String website;
    private String skype;
    private String itemsBuild;
    private Set<String> attachments;
    private String loadingPort;
    @NonNull
    private LocalDate validTill;
    private Boolean status = Boolean.FALSE;
    @NonNull
    private Long cityId;
    @NonNull
    @Size(max = 8000)
    private String contactPerson;
    private String contactSkype;

}
