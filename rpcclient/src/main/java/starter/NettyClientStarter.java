package starter;

import api.HelloObject;
import api.HelloService;
import entity.RpcResponse;
import enumeration.InvokerCode;
import enumeration.SerializerCode;
import futureTask.UnCompletedFuture;
import nettyClient.NettyClient;
import proxy.CglibProxy;
import client.RpcClient;

public class NettyClientStarter {
    public static void main(String[] args) throws Exception {
        RpcClient client = new NettyClient(SerializerCode.KRYO.getCode(),"192.168.1.205:8848");
        //setting service groups
        client.setServiceGroup(HelloService.class,"default");
        /*
        理论上cglib和javassist创建代理，调用速度会更快
         */
        //以javassist的方式创建代理
//        JavassistProxy javassistProxy = new JavassistProxy(client,InvokerCode.FUTURE_INVOKER);
//        HelloService helloService = javassistProxy.getProxy(HelloService.class);
        //以jdk的方式创建代理
        //JDKProxy JDKProxy = new JDKProxy(client, InvokerCode.FUTURE_INVOKER);
        //HelloService helloService = JDKProxy.getProxy(HelloService.class);
        //以cglib的方式创建代理
        CglibProxy cglibProxy = new CglibProxy(client,InvokerCode.FUTURE_INVOKER);
        HelloService helloService = cglibProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12,"netty message");
//        String res = (String) helloService.hello(object);
//        System.out.println(res);
//        helloService.hello(object);
        UnCompletedFuture<RpcResponse> res = (UnCompletedFuture<RpcResponse>) helloService.hello(object);
        //所以如果执行顺序不重要，可以在任意地方complete然后获得想要的数据
        System.out.println(res.complete());
        helloService.hello(object);
    }
}
