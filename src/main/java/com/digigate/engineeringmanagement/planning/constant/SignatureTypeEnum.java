package com.digigate.engineeringmanagement.planning.constant;

/**
 * Signature type enum
 *
 * @author Pranoy Das
 */
public enum SignatureTypeEnum {
    CERTIFICATION_FOR_OIL_AND_FUEL(1),
    CERTIFICATION_FOR_RVSM_FLT(2),
    CERTIFICATION_FOR_ETOPS_FLT(3),
    CERTIFICATION_FOR_PFI(4),
    CERTIFICATION_FOR_FLT(5);

    private Integer signatureType;

    SignatureTypeEnum(Integer signatureType) {
        this.signatureType = signatureType;
    }

    public Integer getSignatureType() {
        return this.signatureType;
    }
}
