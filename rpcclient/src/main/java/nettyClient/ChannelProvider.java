package nettyClient;

import codec.CommonDecoder;
import codec.CommonEncoder;
import handlers.CommonClientHandler;
import handlers.ConnectorIdleStateTrigger;
import handlers.FutureClientHandler;
import idle.ConnectionWatchdog;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import org.apache.log4j.Logger;
import serializer.CommonSerializer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ChannelProvider {
    private static Logger logger = Logger.getLogger(ChannelProvider.class.getClass());
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();
    //<服务名称，对应socket>的本地缓存，避免多次向注册中心请求获取获取
    private static Map<String,InetSocketAddress> socketAddressMap = new ConcurrentHashMap<>();
    //<请求地址,通道>的本地缓存，避免多次获取通道，实现通道复用
    private static Map<String,Channel> channelMap = new ConcurrentHashMap<>();
    //客户端的心跳事件处理器
    private static final ConnectorIdleStateTrigger idleStateTrigger = new ConnectorIdleStateTrigger();
    //重连时使用的异步定时处理器
    private static final HashedWheelTimer timer = new HashedWheelTimer();

    //建立到服务端的连接
    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer commonSerializer,CommonClientHandler clientHandler){
        String key = inetSocketAddress.toString()+commonSerializer.getCode();
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            if(channelMap!=null&&channel.isActive()){
                return channel;
            }else {
                channelMap.remove(key);
            }
        }

        final ConnectionWatchdog watchdog = new ConnectionWatchdog(bootstrap, timer, inetSocketAddress, true,12) {
            public ChannelHandler[] handlers() {
                return new ChannelHandler[] {
                        //connectionWatchdog本身也是handler，必须加入pipeline
                        this,
                        new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS),
                        idleStateTrigger,
                        new CommonDecoder(),
                        new CommonEncoder(commonSerializer),
                        clientHandler
                };
            }
        };

        Channel channel = null;
        try {
            synchronized (bootstrap){
                bootstrap.handler(new ChannelInitializer<SocketChannel>(){
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(watchdog.handlers());
                    }
                });
                channel = connect(bootstrap,inetSocketAddress);
            }
        }catch (ExecutionException e){
            logger.error("连接失败");
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channelMap.put(key,channel);
        return channel;
    }

    //尝试连接客户端
    public static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener)future->{
            if(future.isSuccess()){
                logger.info("客户端连接成功");
                completableFuture.complete(future.channel());
            }
        });
        //阻塞的获得future的结果
        return completableFuture.get();
    }

    public static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //是否开启 TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                //TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }

    //将服务对应地址放入缓存
    public static void putServiceAddress(String serviceSign,InetSocketAddress inetSocketAddress){
        if(socketAddressMap.containsKey(serviceSign)){
            socketAddressMap.remove(serviceSign);
        }
        socketAddressMap.put(serviceSign,inetSocketAddress);
    }

    //查找服务对应地址
    public static InetSocketAddress getServiceAddress(String serviceSign){
        return socketAddressMap.get(serviceSign);
    }
}
