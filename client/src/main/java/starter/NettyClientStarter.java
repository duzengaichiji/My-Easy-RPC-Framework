package starter;

import api.HelloObject;
import api.HelloService;
import enumeration.SerializerCode;
import nettyClient.NettyClient;
import proxy.RpcClientProxy;
import rpcInterfaces.RpcClient;

public class NettyClientStarter {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(SerializerCode.KRYO.getCode());
        //setting service groups
        client.setServiceGroup(HelloService.class,"default");
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12,"netty message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
