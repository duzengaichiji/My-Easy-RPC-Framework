package com.nhx.common.enumeration;

public enum InvokerCode {
    SYNC_INVOKER(0),
    FUTURE_INVOKER(1),
    ONEWAY_INVOKER(2),
    CALLBACK_INVOKER(3);

    private final int code;

    InvokerCode(int code) {
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}
