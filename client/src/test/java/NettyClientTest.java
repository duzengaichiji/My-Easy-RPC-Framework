import enumeration.SerializerCode;
import nettyClient.HeartBeatClient;
import proxy.RpcClientProxy;
import api.HelloObject;
import api.HelloService;
import nettyClient.NettyClient;
import org.junit.Test;
import rpcInterfaces.RpcClient;

import java.util.concurrent.TimeUnit;

public class NettyClientTest {
    @Test
    public void nettyClientTest() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RpcClient client = new NettyClient(SerializerCode.KRYO.getCode());
                //setting service groups
                client.setServiceGroup(HelloService.class,"group1");
                RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
                HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
                HelloObject object = new HelloObject(12,"netty message");
                String res = helloService.hello(object);
                System.out.println(res);

            }
        }).start();

        //new HeartBeatClient().connect(9000, "127.0.0.1");
        //请求一次之后将注册中心关闭，仍然能成功方位服务端
//        TimeUnit.SECONDS.sleep(5);
//        System.out.println("===================================");
//        res = helloService.hello(object);
//        System.out.println(res);
    }
}
