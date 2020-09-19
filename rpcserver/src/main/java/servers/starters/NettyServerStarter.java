package servers.starters;

import annotation.ServiceScan;
import api.HelloService;
import enumeration.LoadBalancerCode;
import enumeration.RegistryCode;
import enumeration.SerializerCode;
import impl.HelloServiceImpl;
import servers.nettyServer.NettyServer;

@ServiceScan(value = "impl")
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
