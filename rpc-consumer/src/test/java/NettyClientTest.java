import org.junit.Test;

public class NettyClientTest {
    @Test
    public void nettyClientTest() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                RpcClient client = new NettyClient(SerializerCode.KRYO.getCode());
//                //setting service groups
//                client.setServiceGroup(HelloService.class,"group1");
//                JDKProxy JDKProxy = new JDKProxy(client);
//                HelloService helloService = JDKProxy.getProxy(HelloService.class);
//                HelloObject object = new HelloObject(12,"netty message");
//                String res = helloService.hello(object);
//                System.out.println(res);

            }
        }).start();
    }
}
