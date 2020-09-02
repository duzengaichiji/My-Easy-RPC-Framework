package nettyClient;

import codec.CommonDecoder;
import codec.CommonEncoder;
import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.RpcError;
import exception.RpcException;
import factory.SingleTonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import registry.NacosServiceRegistryCenter;
import registry.ServiceRegistryCenter;
import results.UnProcessedResponse;
import rpcInterfaces.RpcClient;
import serializer.CommonSerializer;
import serializer.JsonSerializer;
import serializer.KryoSerializer;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {

    private final static Bootstrap bootstrap;
    private static final EventLoopGroup group;
    private UnProcessedResponse unProcessedResponse;

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class);
    }

    //远程服务注册中心
    private ServiceRegistryCenter serviceRegistryCenter;
    //序列化器
    private CommonSerializer serializer;

    public NettyClient(Integer serializer){
        this.serviceRegistryCenter = new NacosServiceRegistryCenter();
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unProcessedResponse = SingleTonFactory.getInstance(UnProcessedResponse.class);
    }

    //将结果封装进CompletableFuture，异步的处理请求结果
    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest request) {
        if(serializer==null){
            System.out.println("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        //用future接收结果的好处就是，可以在任何需要的时候去获取，而不是必须在当前就阻塞的等待服务端对请求的处理
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            //通过注册中心获取服务的 套接字，直接通过套接字就能发送请求
            InetSocketAddress inetSocketAddress = serviceRegistryCenter.lookupService(request.getInterfactName());
            Channel channel = ChannelProvider.get(inetSocketAddress,serializer);
            if(!channel.isActive()){
                group.shutdownGracefully();
                System.out.println("服务端通道未打开");
                return null;
            }
            //将future结果存入未完成列表，在需要的地方取出
            unProcessedResponse.put(request.getRequestId(),resultFuture);
            channel.writeAndFlush(request).addListener(future1->{
                if(future1.isSuccess()){
                    System.out.println("客户端发送消息"+ request.toString());
                }else{
                    resultFuture.completeExceptionally(future1.cause());
                    System.out.println("发送消息时有错误"+future1.cause());
                }
            });
            /*
            //这种是同步等待的方式，需要阻塞等待服务端送回结果
            channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            RpcResponse rpcResponse = channel.attr(key).get();
            return rpcResponse.getData();
             */
        }catch (Exception e){
            //System.out.println(e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}
