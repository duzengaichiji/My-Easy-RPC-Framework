package com.nhx.core.registry;

import com.nhx.core.entity.RpcRequest;

//注册中心
public interface ServiceRegistry {
    //服务注册
    <T> void register(T service,String... groupId);
    //获取服务
    Object getService(RpcRequest rpcRequest);

    int getCode();

    static ServiceRegistry getByCode(int code){
        switch (code){
            case 0:
                return new DefaultServiceRegistry();
            case 1:
                return new MultiImplServiceRegistry();
            case 2:
                return new GroupServiceRegistry();
            default:
                return null;
        }
    }
}
