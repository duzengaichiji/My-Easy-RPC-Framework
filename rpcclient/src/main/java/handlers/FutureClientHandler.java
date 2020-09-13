package handlers;

import entity.RpcResponse;
import enumeration.ClientHandlerCode;
import factory.SingleTonFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import futureTask.UnProcessedResponse;

@ChannelHandler.Sharable
public class FutureClientHandler extends SimpleChannelInboundHandler<RpcResponse> implements CommonClientHandler{

    private UnProcessedResponse unProcessedResponse;

    public FutureClientHandler() {
        //半成品状态的请求列表全局只有一个
        unProcessedResponse = SingleTonFactory.getInstance(UnProcessedResponse.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        try {
            System.out.println("客户端收到信息"+rpcResponse);
            unProcessedResponse.complete(rpcResponse);
        }finally {
            //
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public int getCode() {
        return ClientHandlerCode.FUTUREHANDLER.getCode();
    }
}
