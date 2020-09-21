package com.nhx.provider.server;

public interface RpcServer {
    void start();

    <T> void publishService(Object service,Class<T> serviceClass,String... groupId);
}
