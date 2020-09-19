package handlers;

import entity.RpcResponse;
import enumeration.ClientHandlerCode;
import factory.SingleTonFactory;
import futureTask.UnProcessedResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.apache.log4j.Logger;
import sun.rmi.runtime.Log;

public class SyncClientHandler extends SimpleChannelInboundHandler<RpcResponse> implements CommonClientHandler{
    private static Logger logger = Logger.getLogger(SyncClientHandler.class.getClass());
    private UnProcessedResponse unProcessedResponse;

    public SyncClientHandler() {
        unProcessedResponse = SingleTonFactory.getInstance(UnProcessedResponse.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
            //这种方式，每次都要阻塞等待服务端传回数据
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
        return ClientHandlerCode.SYNCHANDLER.getCode();
    }
}
