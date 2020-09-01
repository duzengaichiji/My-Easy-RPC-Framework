package util;

import factory.ThreadPoolFactory;
import registry.ServiceRegistryCenter;

import java.util.concurrent.ExecutorService;

public class ShutdownHook {
    private final ExecutorService executorService = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");
    private static final ShutdownHook shutdownHook = new ShutdownHook();
    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    public void addClearAllHook(ServiceRegistryCenter serviceRegistryCenter){
        //Runtime是当前虚拟机运行时环境对象，
        //调用addShutDown可以为其添加一个钩子函数，虚拟机关闭时候调用
        //该函数会创建一个新线程，调用注销服务的方法；
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            serviceRegistryCenter.clearRegistry();
            executorService.shutdown();
        }));
    }
}
