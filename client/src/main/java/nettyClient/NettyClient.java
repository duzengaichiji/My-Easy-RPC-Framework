package nettyClient;

import codec.CommonDecoder;
import codec.CommonEncoder;
import entity.RpcRequest;
import entity.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import registry.NacosServiceRegistryCenter;
import registry.ServiceRegistryCenter;
import rpcInterfaces.RpcClient;
import serializer.CommonSerializer;
import serializer.JsonSerializer;
import serializer.KryoSerializer;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {

    private final static Bootstrap bootstrap;
    private static final EventLoopGroup group;

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
    }

    @Override
    public Object sendRequest(RpcRequest request) {
        if(serializer==null){
            System.out.println("未设置序列化器");
            return null;
        }
        Object result = null;
        try {
            //通过注册中心获取服务的 套接字，直接通过套接字就能发送请求
            InetSocketAddress inetSocketAddress = serviceRegistryCenter.lookupService(request.getInterfactName());
            Channel channel = ChannelProvider.get(inetSocketAddress,serializer);
            if(channel!=null){
                channel.writeAndFlush(request).addListener(future1->{
                    if(future1.isSuccess()){
                        System.out.println("客户端发送消息"+ request.toString());
                    }else{
                        System.out.println("发送消息时有错误"+future1.cause());
                    }
                });
                //关闭通道
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        }catch (InterruptedException e){
            //System.out.println(e);
        }
        return null;
    }

}
