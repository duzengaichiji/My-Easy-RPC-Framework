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
//        HelloService helloService = new HelloServiceImpl();
//
//        NettyServer server = new NettyServer("127.0.0.1",9000,SerializerCode.KRYO.getCode(), RegistryCode.GroupImpl.getCode());
//        server.publishService(helloService,HelloService.class,"group1");
//        server.start();

        try {
            Thread.currentThread().getContextClassLoader().loadClass("servers.nettyServer.NettyServer");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
