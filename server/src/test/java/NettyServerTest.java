import api.HelloService;
import impl.HelloServiceImpl;
import org.junit.Test;
import servers.nettyServer.NettyServer;

public class NettyServerTest {
    @Test
    public void nettyServerTest(){
        HelloService helloService = new HelloServiceImpl();

        NettyServer server = new NettyServer("127.0.0.1",9000,0);
        server.publishService(helloService,HelloService.class);
        server.start();
    }
}
