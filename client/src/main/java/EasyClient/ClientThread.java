package EasyClient;

import api.HelloObject;
import api.HelloService;

import java.util.concurrent.Callable;

public class ClientThread implements Runnable {

    @Override
    public void run() {
//        RpcClientProxy proxy = new RpcClientProxy("127.0.0.1",9000);
//        HelloService helloService = proxy.getProxy(HelloService.class);
//        HelloObject helloObject = new HelloObject(1,"thread"+Thread.currentThread().getName());
//        String res = helloService.hello(helloObject);
//        System.out.println(res);
    }
}
