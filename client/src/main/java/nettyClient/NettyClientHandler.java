package nettyClient;

import entity.RpcResponse;
import factory.SingleTonFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import results.UnProcessedResponse;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private UnProcessedResponse unProcessedResponse;

    public NettyClientHandler() {
        //半成品状态的请求列表全局只有一个
        unProcessedResponse = SingleTonFactory.getInstance(UnProcessedResponse.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        try {
            System.out.println("客户端收到信息"+rpcResponse);
            /*
            //这种方式，每次都要阻塞等待服务端传回数据
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            channelHandlerContext.channel().attr(key).set(rpcResponse);
            channelHandlerContext.close();
             */
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
}
