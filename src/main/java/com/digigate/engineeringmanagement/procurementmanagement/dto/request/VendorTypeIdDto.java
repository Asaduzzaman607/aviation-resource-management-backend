package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.configurationmanagement.constant.VendorType;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.util.Objects;

/**
 * Vendors Type and Id dto
 *
 * @author Sayem Hasnat
 */
@Getter
@Setter
@Value(staticConstructor = "of")
public class VendorTypeIdDto {

    Long vendorId;
    VendorType vendorType;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorTypeIdDto that = (VendorTypeIdDto) o;
        return Objects.equals(getVendorId(), that.getVendorId()) &&
                Objects.equals(getVendorType(), that.getVendorType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVendorId(), getVendorType());
    }
}

