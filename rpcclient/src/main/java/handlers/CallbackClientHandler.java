package handlers;

import entity.RpcResponse;
import enumeration.ClientHandlerCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class CallbackClientHandler extends SimpleChannelInboundHandler<RpcResponse> implements CommonClientHandler{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public int getCode() {
        return ClientHandlerCode.CALLBACKHANDLER.getCode();
    }
}
