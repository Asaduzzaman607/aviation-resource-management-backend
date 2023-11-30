package com.digigate.engineeringmanagement.storemanagement.payload.response;

import com.digigate.engineeringmanagement.storemanagement.payload.projection.StoreReturnPartDetailsProjection;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Objects;

@Data
@Builder
public class StoreReturnDetailsViewModel {
    @JsonProperty(value = "storeReturnPartStoreReturnId")
    private Long detailsId; //TODO: change in frontend, currently solved with jsonproperty
    @JsonProperty(value = "storeReturnPartStoreReturnVoucherNo")
    private String voucherNo;
    private String caabStatus;
    private String caabRemarks;
    private String caabCheckbox;
    private String approvalAuthNo;
    private LocalDate authorizedDate;
    private LocalDate authorizesDate;
    private String certApprovalRef;
    private String authorizedUser;
    private String authorizesUser;
    private Boolean caabEnabled;

    public static StoreReturnDetailsViewModel from(StoreReturnPartDetailsProjection projection) {
        StoreReturnDetailsViewModelBuilder storeReturnDetailsViewModelBuilder = StoreReturnDetailsViewModel.builder()
                .detailsId(projection.getId())
                .voucherNo(projection.getStoreReturnPartStoreReturnVoucherNo())
                .caabStatus(projection.getCaabStatus())
                .caabRemarks(projection.getCaabRemarks())
                .caabCheckbox(projection.getCaabCheckbox())
                .approvalAuthNo(projection.getApprovalAuthNo())
                .authorizedDate(projection.getAuthorizedDate())
                .authorizesDate(projection.getAuthorizesDate())
                .certApprovalRef(projection.getCertApprovalRef())
                .caabEnabled(projection.getCaabEnabled());

        if (Objects.nonNull(projection.getAuthorizedUser())) {
            storeReturnDetailsViewModelBuilder.authorizedUser(projection.getAuthorizedUser().getName());
        }

        if (Objects.nonNull(projection.getAuthorizesUser())) {
            storeReturnDetailsViewModelBuilder.authorizesUser(projection.getAuthorizesUser().getName());
        }

        return storeReturnDetailsViewModelBuilder.build();
    }
}
