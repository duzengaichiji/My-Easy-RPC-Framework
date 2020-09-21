package com.nhx.common.enumeration;

@Deprecated
public enum  ClientHandlerCode {
    SYNCHANDLER(0),
    FUTUREHANDLER(1),
    ONEWAYHANDLER(2),
    CALLBACKHANDLER(3);

    private final int code;

    ClientHandlerCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
