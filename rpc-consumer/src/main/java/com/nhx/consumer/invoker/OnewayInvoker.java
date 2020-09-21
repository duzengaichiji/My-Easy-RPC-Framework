package com.nhx.consumer.invoker;

import com.nhx.core.entity.RpcRequest;
import com.nhx.common.enumeration.RpcError;
import com.nhx.common.exception.RpcException;
import io.netty.channel.Channel;
import com.nhx.consumer.client.RpcClient;
import org.apache.log4j.Logger;

//不接收结果，只负责发送
public class OnewayInvoker implements Invoker{
    private static Logger logger = Logger.getLogger(OnewayInvoker.class.getClass());
    private RpcClient rpcClient;

    public OnewayInvoker(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public Object invoke(Channel channel, RpcRequest request) {
        try {
            //OneWay比较简单，只负责发送，不管对方接收了没有，也不管对方是否回复
            channel.writeAndFlush(request).addListener(future1->{
                if(future1.isSuccess()){
                    logger.info("客户端发送消息:"+ request.toString());
                }else{
                    logger.error("发送消息时有错误:"+future1.cause());
                }
            });
        }catch (Exception e){
            logger.error("发送失败s");
            Thread.currentThread().interrupt();
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        return null;
    }
}
