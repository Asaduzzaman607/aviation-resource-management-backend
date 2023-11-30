package com.digigate.engineeringmanagement.planning.constant;

public enum PackageType {
    WORK_PACKAGE_SUMMARY(0),
    WORK_PACKAGE_CERTIFICATE_RECORD(1);

    private final Integer id;
    PackageType(int id) {
        this.id = id;
    }
}
