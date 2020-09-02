import api.HelloService;
import enumeration.RegistryCode;
import enumeration.SerializerCode;
import impl.HelloServiceImpl;
import org.junit.Test;
import serializer.CommonSerializer;
import servers.nettyServer.NettyServer;

public class NettyServerTest {
    @Test
    public void nettyServerTest(){
        HelloService helloService = new HelloServiceImpl();

        NettyServer server = new NettyServer("127.0.0.1",9000, RegistryCode.GroupImpl.getCode(), SerializerCode.KRYO.getCode());
        server.publishService(helloService,HelloService.class);
        server.start();
    }
}
