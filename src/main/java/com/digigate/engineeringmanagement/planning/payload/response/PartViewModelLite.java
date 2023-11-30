package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * this view model responsible to viewing Part id & Part No.
 *
 * @author ashinisingha
 */
@NoArgsConstructor
@Getter
@Setter
public class PartViewModelLite {
    private Long partId;
    private String partNo;


    public PartViewModelLite(Long partId, String partNo) {
        this.partId = partId;
        this.partNo = partNo;
    }

}
