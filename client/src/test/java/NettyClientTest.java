import EasyClient.RpcClientProxy;
import api.HelloObject;
import api.HelloService;
import nettyClient.NettyClient;
import org.junit.Test;
import rpcInterfaces.RpcClient;

public class NettyClientTest {
    @Test
    public void nettyClientTest(){
        RpcClient client = new NettyClient("127.0.0.1",9000);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12,"netty message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
