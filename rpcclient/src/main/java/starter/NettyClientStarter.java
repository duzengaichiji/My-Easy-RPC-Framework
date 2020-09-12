package starter;

import api.HelloObject;
import api.HelloService;
import enumeration.InvokerCode;
import enumeration.SerializerCode;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import nettyClient.NettyClient;
import proxy.CglibProxy;
import proxy.JDKProxy;
import proxy.JavassistProxy;
import rpcInterfaces.RpcClient;

public class NettyClientStarter {
    public static void main(String[] args) throws Exception {
        RpcClient client = new NettyClient(SerializerCode.KRYO.getCode());
        //setting service groups
        client.setServiceGroup(HelloService.class,"default");
        /*
        理论上cglib和javassist创建代理，调用速度会更快
         */
        //以jdk的方式创建代理
        //JDKProxy JDKProxy = new JDKProxy(client, InvokerCode.FUTURE_INVOKER);
        //HelloService helloService = JDKProxy.getProxy(HelloService.class);
        //以cglib的方式创建代理
        CglibProxy cglibProxy = new CglibProxy(client,InvokerCode.FUTURE_INVOKER);
        HelloService helloService = cglibProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12,"netty message");
        String res = helloService.hello(object);
        System.out.println(res);
        //以javassist的方式创建代理
//        JavassistProxy javassistProxy = new JavassistProxy(client,InvokerCode.FUTURE_INVOKER);
//        HelloService helloService = javassistProxy.getProxy(HelloService.class);
//
//        HelloObject object = new HelloObject(12,"netty message");
//        helloService.invokeMethod(HelloService.class.getDeclaredMethod("hello", HelloObject.class),
//                new Object[]{object});
    }
}
