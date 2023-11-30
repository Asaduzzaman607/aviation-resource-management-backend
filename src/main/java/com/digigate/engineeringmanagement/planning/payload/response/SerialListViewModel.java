package com.digigate.engineeringmanagement.planning.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * this view model responsible to viewing Serial List.
 *
 * @author Md. Imam Hasan
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SerialListViewModel {
    private String partNo;
    private String serialNumber;

}



