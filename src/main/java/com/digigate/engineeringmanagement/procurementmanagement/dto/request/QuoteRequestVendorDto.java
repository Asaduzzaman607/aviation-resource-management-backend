package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import com.digigate.engineeringmanagement.common.payload.IDto;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Quote Request Vendors dto
 *
 * @author Sayem Hasnat
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuoteRequestVendorDto implements IDto {
    private Long id;
    @NotNull
    private LocalDate requestDate;
    @NotNull
    private Long vendorId;
}
