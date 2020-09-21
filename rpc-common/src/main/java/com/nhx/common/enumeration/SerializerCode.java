package com.nhx.common.enumeration;

public enum SerializerCode {

    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOBUF(3);

    private final int code;

    SerializerCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
