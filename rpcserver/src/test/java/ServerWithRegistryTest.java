import api.HelloService;
import impl.HelloServiceImpl;
import org.junit.Test;
import registry.DefaultServiceRegistry;
import registry.ServiceRegistry;
import servers.serverWithRegistry.RpcServerWithRegistry;

public class ServerWithRegistryTest {
    @Test
    public void testServerWithRegistry(){
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        //System.out.println(serviceRegistry.getServiceMap().keySet());
        RpcServerWithRegistry rpcServerWithRegistry = new RpcServerWithRegistry(serviceRegistry);
        rpcServerWithRegistry.start(9000);
    }
}
