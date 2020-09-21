package com.nhx.consumer.invoker;

import com.nhx.consumer.nettyClient.NettyClient;
import com.nhx.core.entity.RpcRequest;
import com.nhx.core.entity.RpcResponse;
import com.nhx.common.enumeration.RpcError;
import com.nhx.common.exception.RpcException;
import com.nhx.common.factory.SingleTonFactory;
import com.nhx.core.futureTask.UnCompletedFuture;
import com.nhx.core.futureTask.UnProcessedResponse;
import io.netty.channel.Channel;
import com.nhx.consumer.client.RpcClient;
import org.apache.log4j.Logger;

import java.util.concurrent.*;

public class CallbackInvoker implements Invoker{
    private static Logger logger = Logger.getLogger(CallbackInvoker.class.getClass());
    private RpcClient rpcClient;
    private UnProcessedResponse unProcessedResponse;
    private ExecutorService threadPool;

    public CallbackInvoker(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        this.unProcessedResponse = SingleTonFactory.getInstance(UnProcessedResponse.class);

        int corePoolSize = 4;
        int maximumPoolSize = 20;
        long keepAliveTime = 5;
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

    public CompletableFuture<RpcResponse> sendRequest(Channel channel, RpcRequest rpcRequest){
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            //将future结果存入未完成列表，在需要的地方取出
            unProcessedResponse.put(rpcRequest.getRequestId(),resultFuture);
            channel.writeAndFlush(rpcRequest).addListener(future1->{
                if(future1.isSuccess()){
                    logger.info("客户端发送消息:"+ rpcRequest.toString());
                }else{
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("发送时有错误:" + future1.cause());
                }
            });
        }catch (Exception e){
            logger.error("发送失败");
            Thread.currentThread().interrupt();
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        return resultFuture;
    }

    @Override
    public Object invoke(Channel channel, RpcRequest request) {
        CompletableFuture<RpcResponse> completableFuture = null;
        completableFuture = sendRequest(channel, request);
        UnCompletedFuture unCompletedFuture = new UnCompletedFuture<RpcResponse>(completableFuture,((NettyClient) rpcClient).getExecuteWaitTime());
        //开启新线程进行结果处理
        threadPool.execute(new CallBackWorkerThread(unCompletedFuture));
        return unCompletedFuture.getResultObject();
    }
}
