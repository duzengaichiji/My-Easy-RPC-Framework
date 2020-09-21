package com.nhx.common.enumeration;

public enum  LoadBalancerCode {

    RANDOMLOADBALANCER(0),
    ROUNDROBINLOADBALANDER(1);

    private final int code;

    LoadBalancerCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
