package com.digigate.engineeringmanagement.configurationmanagement.dto.response;

import com.digigate.engineeringmanagement.common.util.Helper;
import lombok.*;

import static com.digigate.engineeringmanagement.common.util.Helper.addBreakLine;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VendorResponseDto {
    private String name;
    private String address;
    private String officePhone;
    private String email;
    private String website;
    private String contactPerson;
    private String contactMobile;
    private String contactPhone;
    private String contactEmail;
    private String contactSkype;

    @Override
    public String toString() {
        return addBreakLine(name, "Name: ")
                + addBreakLine(address, "Address: ")
                + addBreakLine(officePhone, "OfficePhone: ")
                + addBreakLine(email, "Email: ")
                + addBreakLine(website, "Website: ")
                + addBreakLine(contactPerson, "Contact Person: ")
                + addBreakLine(contactMobile, "Contact Mobile: ")
                + addBreakLine(contactPhone, "Contact Phone: ")
                + addBreakLine(contactEmail, "Contact Email: ")
                + addBreakLine(contactSkype, "Contact Skype: ");
    }
}
