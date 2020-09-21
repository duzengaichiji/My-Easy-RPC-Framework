package com.nhx.examples.server;

import com.nhx.examples.api.HelloService;
import com.nhx.core.annotation.ServiceScan;
import com.nhx.common.enumeration.LoadBalancerCode;
import com.nhx.common.enumeration.RegistryCode;
import com.nhx.common.enumeration.SerializerCode;
import com.nhx.examples.impl.HelloServiceImpl;
import com.nhx.provider.nettyServer.NettyServer;

@ServiceScan(value = "com.nhx.examples.impl")
public class NettyServerStarter {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();

        NettyServer server = new NettyServer("127.0.0.1",9000, SerializerCode.KRYO.getCode(), RegistryCode.GroupImpl.getCode(),
                "192.168.137.1:8848",LoadBalancerCode.RANDOMLOADBALANCER.getCode());
        //手动注册服务
        //server.publishService(helloService,HelloService.class,"group1");
        server.start();
    }
}
