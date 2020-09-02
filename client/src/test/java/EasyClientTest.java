import EasyClient.EasyRpcClient;
import api.HelloObject;
import api.HelloService;
import org.junit.Test;
import proxy.RpcClientProxy;
import rpcInterfaces.RpcClient;

import java.util.concurrent.TimeUnit;

public class EasyClientTest {
    @Test
    public void testEasyClient(){
        RpcClient client = new EasyRpcClient("127.0.0.1",9000);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        System.out.println(helloService.getClass());
        HelloObject helloObject = new HelloObject(13,"this is a test message");
        String res = helloService.hello(helloObject);
        //helloService.hello(helloObject);
        System.out.println(res);
    }

    @Test
    public void multiThreadTestEasyClient() throws InterruptedException {
        RpcClient client = new EasyRpcClient("127.0.0.1",9000);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);

        for (int i = 1; i <= 10; i++) {
            final int tempI = i;
            new Thread(() -> {
                HelloObject helloObject = new HelloObject(tempI,"thread"+Thread.currentThread().getName());
                String res = helloService.hello(helloObject);
                System.out.println(res);
            }, String.valueOf(i)).start();
        }
        TimeUnit.SECONDS.sleep(10);
    }
}
