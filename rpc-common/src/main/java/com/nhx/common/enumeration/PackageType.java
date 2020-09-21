package com.nhx.common.enumeration;

public enum  PackageType {
    REQUEST_PACK(0),
    RESPONSE_PACK(1),
    HEARTBEAT_PACK(2);

    PackageType(int code) {
        this.code = code;
    }

    private final int code;

    public int getCode() {
        return code;
    }


}
