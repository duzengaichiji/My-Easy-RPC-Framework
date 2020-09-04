package servers.nettyServer;

import annotation.Service;
import codec.CommonDecoder;
import codec.CommonEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import registry.*;
import rpcInterfaces.RpcServer;
import serializer.CommonSerializer;
import servers.handlers.NettyServerHandler;
import util.ShutdownHook;

import java.net.InetSocketAddress;

public class NettyServer implements RpcServer {

    private String host;//远程注册中心的地址
    private int port;//提供服务的端口
    private final CommonSerializer serializer;//序列化器
    private ServiceRegistry serviceRegistry;//本地服务映射表
    private ServiceRegistryCenter serviceRegistryCenter;//远程服务注册中心

    public NettyServer(String host,int port,Integer serializer,Integer registry){
        this.host = host;
        this.port = port;
        this.serviceRegistryCenter = new NacosServiceRegistryCenter();
        this.serviceRegistry = ServiceRegistry.getByCode(registry);
        this.serializer = CommonSerializer.getByCode(serializer);
    }

    public NettyServer(String host, int port, CommonSerializer serializer, ServiceRegistry serviceRegistry, ServiceRegistryCenter serviceRegistryCenter) {
        this.host = host;
        this.port = port;
        this.serializer = serializer;
        this.serviceRegistry = serviceRegistry;
        this.serviceRegistryCenter = serviceRegistryCenter;
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
                            pipeline.addLast(new CommonEncoder(serializer));//传入编码器，解码器
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler(serviceRegistry));//传入本地注册中心进请求处理器
                        }
                    });
            System.out.println("...server ready...");
            //绑定监听端口,sync是同步阻塞
            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("...binding port:"+port+" server start...");
            //添加服务注销的钩子
            //ShutdownHook.getShutdownHook().addClearAllHook(serviceRegistryCenter);
            //关闭通道
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println("启动服务器时有错误发生: "+e);
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
