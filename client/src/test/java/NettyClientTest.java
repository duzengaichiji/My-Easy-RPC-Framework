import enumeration.SerializerCode;
import proxy.RpcClientProxy;
import api.HelloObject;
import api.HelloService;
import nettyClient.NettyClient;
import org.junit.Test;
import rpcInterfaces.RpcClient;

import java.util.concurrent.TimeUnit;

public class NettyClientTest {
    @Test
    public void nettyClientTest() throws InterruptedException {
        RpcClient client = new NettyClient(SerializerCode.KRYO.getCode());
        //setting service groups
        client.setServiceGroup(HelloService.class,"group1");
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12,"netty message");
        String res = helloService.hello(object);
        System.out.println(res);
        //请求一次之后将注册中心关闭，仍然能成功方位服务端
//        TimeUnit.SECONDS.sleep(30);
//        res = helloService.hello(object);
//        System.out.println(res);
    }
}
