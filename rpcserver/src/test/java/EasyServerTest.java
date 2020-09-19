import api.HelloService;
import impl.HelloServiceImpl;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import servers.easyServer.EasyRpcServer;
import org.junit.Test;

public class EasyServerTest {
    @Test
    public void testEasyServer(){
//        HelloService helloService = new HelloServiceImpl();
//        EasyRpcServer rpcServer = new EasyRpcServer();
//        rpcServer.register(helloService,9000);
//        String relativelyPath=System.getProperty("user.dir");
//        PropertyConfigurator.configure(relativelyPath+ "\\src\\main\\resources\\log4j..properties");
        Logger logger = Logger.getLogger(EasyServerTest.class.getClass());
        logger.info("logging test");
    }
}
