package com.nhx.common.enumeration;

public enum RpcError {
    //未定义
    UNKNOWN_ERROR("出现未知错误"),
    //序列化错误
    UNKNOWN_PROTOCOL("不识别的协议包"),
    UNKNOWN_SERIALIZER("不识别的(反)序列化器"),
    UNKNOWN_PACKAGE_TYPE("不识别的数据包类型"),
    SERIALIZER_NOT_FOUND("找不到序列化器"),
    SERIALIZE_FAIL("序列化失败"),
    DESERIALIZE_FAIL("反序列化失败"),
    //服务端错误
    SERVICE_SCAN_PACKAGE_NOT_FOUND("启动类ServiceScan注解缺失"),
    SERVICE_FAIL("服务调用失败"),
    SERVICE_NOT_FOUND("找不到对应的服务"),
    SERVICE_NOT_IMPLEMENT_ANY_INTERFACE("注册的服务未实现接口"),
    FAILED_TO_CONNECT_TO_SERVICE_REGISTRY("连接注册中心失败"),
    REGISTER_SERVICE_FAILED("注册服务失败"),
    NO_SUCH_INVOKER("没有这种执行器"),
    //客户端错误
    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务端失败"),
    RESPONSE_NOT_MATCH("响应与请求号不匹配");


    private final String message;

    RpcError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
