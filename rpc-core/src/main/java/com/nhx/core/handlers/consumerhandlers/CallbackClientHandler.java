package com.nhx.core.handlers.consumerhandlers;

import com.nhx.core.entity.RpcResponse;
import com.nhx.common.enumeration.ClientHandlerCode;
import com.nhx.common.factory.SingleTonFactory;
import com.nhx.core.futureTask.UnProcessedResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

public class CallbackClientHandler extends SimpleChannelInboundHandler<RpcResponse> implements CommonClientHandler{
    private static Logger logger = Logger.getLogger(CallbackClientHandler.class.getClass());
    private UnProcessedResponse unProcessedResponse;

    public CallbackClientHandler() {
        unProcessedResponse = SingleTonFactory.getInstance(UnProcessedResponse.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        logger.info("客户端收到信息"+rpcResponse);
        unProcessedResponse.complete(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public int getCode() {
        return ClientHandlerCode.CALLBACKHANDLER.getCode();
    }
}
