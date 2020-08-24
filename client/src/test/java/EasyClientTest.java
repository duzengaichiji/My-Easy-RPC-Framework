import api.HelloObject;
import api.HelloService;
import org.junit.Test;
import EasyClient.RpcClientProxy;

public class EasyClientTest {
    @Test
    public void testEasyClient(){
        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1",9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(13,"this is a test message");
        String res = helloService.hello(helloObject);
        System.out.println(res);
    }
}
