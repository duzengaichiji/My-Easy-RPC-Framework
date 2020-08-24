package nettyClient;

import entity.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        try {
            System.out.println("客户端收到信息"+rpcResponse);
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            channelHandlerContext.channel().attr(key).set(rpcResponse);
            channelHandlerContext.close();
        }finally {
            //
        }
    }
}
