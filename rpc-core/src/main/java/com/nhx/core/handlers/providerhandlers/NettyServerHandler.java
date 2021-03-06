package com.nhx.core.handlers.providerhandlers;

import com.nhx.core.entity.HeartbeatRequest;
import com.nhx.core.entity.Request;
import com.nhx.core.entity.RpcRequest;
import com.nhx.core.entity.RpcResponse;
import io.netty.channel.*;
import org.apache.log4j.Logger;
import com.nhx.core.registry.ServiceRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NettyServerHandler extends SimpleChannelInboundHandler<Request> {
    private static Logger logger = Logger.getLogger(NettyServerHandler.class.getClass());
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
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        try{
            //if 接收到心跳请求
            if(request instanceof HeartbeatRequest){
                logger.info(channelHandlerContext.channel().remoteAddress()+" heart beat!!");
                return;
            }else if(request instanceof RpcRequest) {
                Object service = serviceRegistry.getService((RpcRequest) request);//通过本地注册中心获得对应实现对象
                Object result = requestHandler.handler((RpcRequest) request, service);//处理请求
                TimeUnit.SECONDS.sleep(3);//等待3秒，模拟服务端任务
                if (channelHandlerContext.channel().isActive() && channelHandlerContext.channel().isWritable()) {
                    //将结果对象response写入通道
                    ChannelFuture future = channelHandlerContext.writeAndFlush(RpcResponse.success(result, ((RpcRequest) request).getRequestId()));
                    //System.out.println(future);
                } else {
                    logger.error("通道不可写");
                }
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
        logger.info("[Server] : " + channel.remoteAddress().toString().substring(1) + "上线");
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
        logger.info("[Server] : " + channel.remoteAddress().toString().substring(1) + "离线");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}
