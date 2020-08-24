import EasyClient.ClientThread;
import api.HelloObject;
import api.HelloService;
import org.junit.Test;
import EasyClient.RpcClientProxy;

public class EasyClientTest {
    @Test
    public void testEasyClient(){
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1",9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        System.out.println(helloService.getClass());
        HelloObject helloObject = new HelloObject(13,"this is a test message");
        String res = helloService.hello(helloObject);
        //helloService.hello(helloObject);
        System.out.println(res);
    }

    @Test
    public void multiThreadTestEasyClient(){
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1",9000);
        HelloService helloService = proxy.getProxy(HelloService.class);

        for (int i = 1; i <= 10; i++) {
            final int tempI = i;
            HelloObject helloObject = new HelloObject(tempI,"thread"+Thread.currentThread().getName());
            String res = helloService.hello(helloObject);
            //Thread thread = new ClientThread(helloObject,helloService);

        }
    }
}
