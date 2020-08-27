package servers.nettyServer;

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
import registry.ServiceRegistry;
import rpcInterfaces.RpcServer;
import serializer.JsonSerializer;

public class NettyServer implements RpcServer {

    private ServiceRegistry serviceRegistry;

    public NettyServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void start(int port) {
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
                    //.handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG,256)//服务端可连接队列大小
                    .option(ChannelOption.SO_KEEPALIVE,true)//维持长连接
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    //创建一个通道初始化对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {//绑定客户端时触发的操作
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //在pipeline中添加handler类
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new CommonEncoder(new JsonSerializer()));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler(serviceRegistry));
                        }
                    });
            System.out.println("...server ready...");
            //绑定监听端口,sync是同步阻塞
            ChannelFuture future = serverBootstrap.bind(port).sync();
            System.out.println("...binding port:"+port+" server start...");
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
}
