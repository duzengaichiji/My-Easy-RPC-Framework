package servers.nettyServer;

import entity.RpcRequest;
import entity.RpcResponse;
import io.netty.channel.*;
import registry.DefaultServiceRegistry;
import registry.ServiceRegistry;
import servers.serverWithRegistry.RequestHandler;

import java.util.ArrayList;
import java.util.List;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;
    private static List<Channel> channels = new ArrayList<>();

    static {
        requestHandler = new RequestHandler();
    }

    public NettyServerHandler(ServiceRegistry serviceRegistry){
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        try{
            //if 接收到心跳请求
            System.out.println("服务器收到请求："+rpcRequest);
            String interfaceName = rpcRequest.getInterfactName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handler(rpcRequest,service);
            if(channelHandlerContext.channel().isActive()&&channelHandlerContext.channel().isWritable()){
                channelHandlerContext.writeAndFlush(RpcResponse.success(result));
            }
            else{
                System.out.println("通道不可写");
            }
        }finally {
            //Refrence
        }
    }

    /**
     * 通道就绪
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.add(channel);
        System.out.println("[Server] : " + channel.remoteAddress().toString().substring(1) + "上线");
    }

    /**
     * 通道未就绪
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channels.remove(channel);
        System.out.println("[Server] : " + channel.remoteAddress().toString().substring(1) + "离线");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}
