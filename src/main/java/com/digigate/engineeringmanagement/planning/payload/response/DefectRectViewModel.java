package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DefectRect Info ViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefectRectViewModel {
    private String seqNo;
    private String defectDescription;
    private String rectDescription;
}
