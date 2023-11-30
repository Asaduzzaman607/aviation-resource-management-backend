package com.digigate.engineeringmanagement.procurementmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * QuotationNoListDto
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuotationNoListDto {
    private Long quotationId;
    private String quotationNo;
}
