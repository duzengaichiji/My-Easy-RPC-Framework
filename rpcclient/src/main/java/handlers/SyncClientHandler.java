package handlers;

import entity.RpcResponse;
import enumeration.ClientHandlerCode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class SyncClientHandler extends SimpleChannelInboundHandler<RpcResponse> implements CommonClientHandler{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
            //这种方式，每次都要阻塞等待服务端传回数据
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
        channelHandlerContext.channel().attr(key).set(rpcResponse);
        channelHandlerContext.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public int getCode() {
        return ClientHandlerCode.SYNCHANDLER.getCode();
    }
}
