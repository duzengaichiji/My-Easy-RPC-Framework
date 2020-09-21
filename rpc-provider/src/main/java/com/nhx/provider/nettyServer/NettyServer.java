package com.nhx.provider.nettyServer;

import com.nhx.core.codec.CommonDecoder;
import com.nhx.core.codec.CommonEncoder;
import com.nhx.core.registry.NacosServiceRegistryCenter;
import com.nhx.core.registry.ServiceRegistry;
import com.nhx.core.registry.ServiceRegistryCenter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.log4j.Logger;

import com.nhx.provider.server.AbstractRpcServer;
import com.nhx.core.serializer.CommonSerializer;
import com.nhx.core.handlers.providerhandlers.AcceptorIdleStateTrigger;
import com.nhx.core.handlers.providerhandlers.NettyServerHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class NettyServer extends AbstractRpcServer {
    private static Logger logger = Logger.getLogger(NettyServer.class.getClass());
    private String host;//远程注册中心的地址
    private int port;//提供服务的端口
    private final CommonSerializer serializer;//序列化器
    private ServiceRegistry serviceRegistry;//本地服务映射表
    private ServiceRegistryCenter serviceRegistryCenter;//远程服务注册中心
    private final AcceptorIdleStateTrigger acceptorIdleStateTrigger = new AcceptorIdleStateTrigger();//通道状态检测器

    public NettyServer(String host,int port,Integer serializer,Integer registry,String registryAddress,Integer loadBalancer){
        this.host = host;
        this.port = port;
        this.serviceRegistryCenter = new NacosServiceRegistryCenter(registryAddress,loadBalancer);
        this.serviceRegistry = ServiceRegistry.getByCode(registry);
        this.serializer = CommonSerializer.getByCode(serializer);
        //自动扫描并注册服务
        scanServices();
    }

    @Override
    public void start() {
        //Boss线程组专门用来处理连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //worker线程组用来进行数据处理
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            //服务端启动助手，用来配置各种参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    //选择服务端通道的实现类
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG,256)//服务端可连接队列大小
                    .option(ChannelOption.SO_KEEPALIVE,true)//维持长连接
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    //创建一个通道初始化对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {//绑定客户端时触发的操作
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //在pipeline中添加handler类
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //添加通道检测（心跳）处理器，设置每隔一定时间检测读/写事件
                            //这个会检测通道的空闲状态，超过设定的时间没有触发响应的事件（ChannelRead,ChannelWrite）会引发userEventTriggered
                            pipeline.addLast(new IdleStateHandler(5,0,0, TimeUnit.SECONDS));
                            //添加单独的状态处理器
                            pipeline.addLast(acceptorIdleStateTrigger);
                            //服务端的其他部分没有特别之处
                            pipeline.addLast(new CommonEncoder(serializer));//传入编码器，解码器
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler(serviceRegistry));//传入本地注册中心进请求处理器
                        }
                    });
            logger.info("...server ready...");
            //绑定监听端口,sync是同步阻塞
            ChannelFuture future = serverBootstrap.bind(port).sync();
            logger.info("...binding port:"+port+" server start...");
            //添加服务注销的钩子
            //ShutdownHook.getShutdownHook().addClearAllHook(serviceRegistryCenter);
            //监听通道
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: "+e);
        } finally {
            //关闭线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public <T> void publishService(Object service, Class<T> serviceClass,String... groupId) {
        //发布到本地列表
        serviceRegistry.register(service,groupId);
        //发布到注册中心
        serviceRegistryCenter.register(serviceClass.getCanonicalName(),
                new InetSocketAddress(host,port));
    }

}
