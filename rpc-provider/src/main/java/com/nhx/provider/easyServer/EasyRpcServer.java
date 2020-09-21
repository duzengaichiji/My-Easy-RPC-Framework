package com.nhx.provider.easyServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class EasyRpcServer {
    private ExecutorService threadPool = null;
    //private static final Logger logger = LoggerFactory.getLogger(EasyRpcServer.class);

    public EasyRpcServer() {
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                workingQueue,
                threadFactory

        );
    }
    //（serviceImpl对象，端口）
    public void register(Object service,int port){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            //logger.debug("服务器正在启动...");
            System.out.println("服务器正在启动...");
            Socket socket;
            //监听服务开放的端口，收到rpc请求，则交给线程池处理
            while((socket=serverSocket.accept())!=null){
                //logger.debug("客户端链接! ip: "+socket.getInetAddress());
                System.out.println("客户端链接! ip: "+socket.getInetAddress());
                threadPool.execute(new EasyWorkerThread(socket,service));
            }
        }catch (IOException e){
            //logger.error("连接错误,",e);
            System.out.println("连接错误,"+e);
        }
    }
}
