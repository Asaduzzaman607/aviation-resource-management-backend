package com.digigate.engineeringmanagement.storemanagement.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 *UnserviceableComponentListViewModel
 *
 * @author Nafiul Islam
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnserviceableComponentListViewModel {
    private String partNo;
    private String description;
    private String serialNo;
    private String removedFrom;
    private LocalDate removedDate;
    private String reasonRemoved;
    private String rtndById;
    private String rcvdById;
    private String location;
    private String remarks;
}
