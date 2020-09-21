package com.nhx.common.enumeration;

//返回状态码
public enum ResponseCode{
    SUCCESS(200,"方法调用成功"),
    FAIL(500,"调用方法失败"),
    METHOD_NOT_FOUND(501,"未找到指定方法"),
    CLASS_NOT_FOUND(502,"未找到指定类");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
