package servers.handlers;

import entity.RpcRequest;
import entity.RpcResponse;
import io.netty.channel.*;
import registry.ServiceRegistry;
import servers.handlers.RequestHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static RequestHandler requestHandler;//请求处理器
    private static ServiceRegistry serviceRegistry;//本地服务注册中心，记录服务接口和对应的实现了类
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
            Object service = serviceRegistry.getService(rpcRequest);//通过本地注册中心获得对应实现对象
            Object result = requestHandler.handler(rpcRequest,service);//处理请求
            TimeUnit.SECONDS.sleep(5);
            if(channelHandlerContext.channel().isActive()&&channelHandlerContext.channel().isWritable()){
                //将结果对象response写入通道
                channelHandlerContext.writeAndFlush(RpcResponse.success(result,rpcRequest.getRequestId()));
            }
            else{
                System.out.println("通道不可写");
            }
        }finally {
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
