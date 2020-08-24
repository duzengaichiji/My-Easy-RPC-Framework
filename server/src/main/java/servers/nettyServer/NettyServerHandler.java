package servers.nettyServer;

import entity.RpcRequest;
import entity.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import registry.DefaultServiceRegistry;
import registry.ServiceRegistry;
import servers.serverWithRegistry.RequestHandler;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;

    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        try{
            System.out.println("服务器收到请求："+rpcRequest);
            String interfaceName = rpcRequest.getInterfactName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handler(rpcRequest,service);
            ChannelFuture future = channelHandlerContext.writeAndFlush(RpcResponse.success(result));
            future.addListener(ChannelFutureListener.CLOSE);
        }finally {
            //Refrence
        }
    }
}
