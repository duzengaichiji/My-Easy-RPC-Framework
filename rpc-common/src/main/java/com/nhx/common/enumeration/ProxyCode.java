package com.nhx.common.enumeration;

public enum ProxyCode {
    JDK_PROXY(0),
    CGLIB_PROXY(1),
    JAVASSIST_PROXY(2);

    private final int code;

    ProxyCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
