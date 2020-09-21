package com.nhx.consumer.invoker;

import com.nhx.consumer.nettyClient.NettyClient;
import com.nhx.core.entity.RpcRequest;
import com.nhx.core.entity.RpcResponse;
import com.nhx.common.enumeration.RpcError;
import com.nhx.common.exception.RpcException;
import com.nhx.common.factory.SingleTonFactory;
import com.nhx.core.futureTask.UnProcessedResponse;
import io.netty.channel.Channel;
import com.nhx.consumer.client.RpcClient;
import org.apache.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SyncInvoker implements Invoker{
    private static Logger logger = Logger.getLogger(SyncInvoker.class.getClass());
    private RpcClient rpcClient;
    private UnProcessedResponse unProcessedResponse;

    public SyncInvoker(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        this.unProcessedResponse = SingleTonFactory.getInstance(UnProcessedResponse.class);
    }



    @Override
    public Object invoke(Channel channel, RpcRequest request) {
        //这种是同步等待的方式，需要阻塞等待服务端送回结果
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            //将future结果存入未完成列表，在需要的地方取出
            unProcessedResponse.put(request.getRequestId(),resultFuture);
            channel.writeAndFlush(request).addListener(future1->{
                if(future1.isSuccess()){
                    logger.info("客户端发送消息:"+ request.toString());
                }else{
                    resultFuture.completeExceptionally(future1.cause());
                }
            });
        }catch (Exception e){
            logger.error("发送失败");
            Thread.currentThread().interrupt();
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }

        //与Future不同的地方在于，这里直接让future阻塞调用get
        RpcResponse rpcResponse = null;
        try {
            rpcResponse = resultFuture.get(((NettyClient) rpcClient).getExecuteWaitTime(), TimeUnit.SECONDS);
            return rpcResponse.getData();
        } catch (Exception e) {
            logger.error("服务调用失败");
            e.printStackTrace();
            throw new RpcException(RpcError.SERVICE_FAIL);
        }
    }
}
