import api.HelloService;
import impl.HelloServiceImpl;
import servers.easyServer.EasyRpcServer;
import org.junit.Test;

public class EasyServerTest {
    @Test
    public void testEasyServer(){
        HelloService helloService = new HelloServiceImpl();
        EasyRpcServer rpcServer = new EasyRpcServer();
        rpcServer.register(helloService,9000);
    }
}
