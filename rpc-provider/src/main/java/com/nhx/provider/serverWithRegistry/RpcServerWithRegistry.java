package com.nhx.provider.serverWithRegistry;

import com.nhx.core.registry.ServiceRegistry;
import com.nhx.core.handlers.providerhandlers.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RpcServerWithRegistry {
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 50;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    private ExecutorService threadPool;
    private RequestHandler requestHandler = new RequestHandler();
    private ServiceRegistry serviceRegistry;

    public RpcServerWithRegistry(ServiceRegistry serviceRegistry){
        this.serviceRegistry = serviceRegistry;
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,KEEP_ALIVE_TIME,TimeUnit.SECONDS,blockingQueue,threadFactory);
    }

    public void start(int port){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket;
            while ((socket = serverSocket.accept())!=null){
                System.out.println("消费者连接:"+socket.getInetAddress()+" "+socket.getPort());
                threadPool.execute(new RequesthandlerThread(socket,requestHandler,serviceRegistry));
            }
            threadPool.shutdown();
        }catch (IOException e){
            System.out.println("错误"+e);
        }
    }
}
