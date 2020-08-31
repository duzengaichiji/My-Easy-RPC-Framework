package util;

import factory.ThreadPoolFactory;
import sun.nio.ch.ThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public class ShutdownHook {
    private final ExecutorService executorService = ThreadPoolFactory.createDefaultThreadPool("shutdown-hook");
    private static final ShutdownHook shutdownHook = new ShutdownHook();
    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    public void addClearAllHook(){
        System.out.println("关闭本机，注销本机上的所有服务");
        //Runtime是当前虚拟机运行时环境对象，
        //调用addShutDown可以为其添加一个钩子函数，虚拟机关闭时候调用
        //该函数会创建一个新线程，调用注销服务的方法；
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            executorService.shutdown();
        }));
    }
}
