package com.digigate.engineeringmanagement.planning.payload.response;

import com.digigate.engineeringmanagement.planning.constant.PartClassification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Enumerated;

/**
 * this view model responsible to viewing Part list.
 *
 * @author Md. Imam Hasan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartListViewModel {
    private String modelName;
    private String partNo;
    private String description;
    private PartClassification classification;
    private String unitOfMeasureCode;

}

