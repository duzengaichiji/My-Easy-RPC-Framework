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

import java.util.concurrent.TimeUnit;

public class NettyClientStarter {
    public static void main(String[] args) throws Exception {
        RpcClient client = new NettyClient(SerializerCode.KRYO.getCode(),"192.168.137.1:8848",5,50,5,50);
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
        long startTime = System.currentTimeMillis();
        CglibProxy cglibProxy = new CglibProxy(client,InvokerCode.FUTURE_INVOKER);
        HelloService helloService = cglibProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12,"netty message");

//        helloService.hello(object);
//        helloService.hello(object);
//        TimeUnit.SECONDS.sleep(5);//模拟客户端线程的其他任务
//        long endTime = System.currentTimeMillis();
//        System.out.println("twice call cost: "+String.valueOf(endTime-startTime));

        UnCompletedFuture<RpcResponse> res = (UnCompletedFuture<RpcResponse>) helloService.hello(object);
        //所以如果执行顺序不重要，可以在任意地方complete然后获得想要的数据
        UnCompletedFuture<RpcResponse> res1 = (UnCompletedFuture<RpcResponse>) helloService.hello(object);
        TimeUnit.SECONDS.sleep(5);
        res.complete();
        res1.complete();
        long endTime = System.currentTimeMillis();
        System.out.println("twice call cost: "+String.valueOf(endTime-startTime));
    }
}
