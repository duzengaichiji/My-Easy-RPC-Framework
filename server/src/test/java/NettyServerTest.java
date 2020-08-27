import api.HelloService;
import impl.HelloServiceImpl;
import org.junit.Test;
import registry.DefaultServiceRegistry;
import registry.ServiceRegistry;
import servers.nettyServer.NettyServer;

public class NettyServerTest {
    @Test
    public void nettyServerTest(){
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer(registry);
        server.start(9000);
    }
}
