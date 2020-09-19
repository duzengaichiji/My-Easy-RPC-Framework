package nettyClient;

import entity.RpcRequest;
import enumeration.RpcError;
import exception.RpcException;
import handlers.CommonClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;
import registry.NacosServiceRegistryCenter;
import registry.ServiceRegistryCenter;
import client.AbstractRpcClient;
import serializer.CommonSerializer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyClient extends AbstractRpcClient {
    private static Logger logger = Logger.getLogger(NettyClient.class.getClass());
    private final static Bootstrap bootstrap;
    private final static EventLoopGroup group;
    //用来映射每个服务对应的分组，因为一个服务可能有多种实现
    private static Map<String,String> serviceGroupMap;

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class);
        serviceGroupMap = new ConcurrentHashMap<String,String>();
    }

    //远程服务注册中心
    private ServiceRegistryCenter serviceRegistryCenter;
    //序列化器
    private CommonSerializer serializer;

    public NettyClient(Integer serializer,String registryAddress){
        this.serviceRegistryCenter = new NacosServiceRegistryCenter(registryAddress);
        this.serializer = CommonSerializer.getByCode(serializer);
    }

    public NettyClient(Integer serializer,String registryAddress,int connectRetries,int connectWaitTime){
        this(serializer,registryAddress);
        this.setConnectRetries(connectRetries);
        this.setConnectWaitTime(connectWaitTime);
    }

    public NettyClient(Integer serializer,String registryAddress,int connectRetries,int connectWaitTime,int executeRetries,int executeWaitTime){
        this(serializer,registryAddress);
        this.setExecuteRetries(executeRetries);
        this.setExecuteWaitTime(executeWaitTime);
        this.setConnectWaitTime(connectWaitTime);
        this.setConnectRetries(connectRetries);
    }

    public static Map<String, String> getServiceGroupMap() {
        return serviceGroupMap;
    }

    @Override
    public void setServiceGroup(Class service, String groupId) {
        if(serviceGroupMap.containsKey(service.getCanonicalName())){
            serviceGroupMap.remove(service.getCanonicalName());
        }
        serviceGroupMap.put(service.getCanonicalName(),groupId);
    }

    public static void setServiceGroupMap(Map<String, String> serviceGroupMap) {
        NettyClient.serviceGroupMap = serviceGroupMap;
    }

    //分离连接通道的方法和发送请求的方法，以便多次重试
    @Override
    public Channel getChannel(RpcRequest request, CommonClientHandler clienthandler) {
        if(serializer==null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }

        String serviceSign = request.getInterfactName()+request.getGroupId();
        //从缓存中查找服务对应的socket地址
        InetSocketAddress inetSocketAddress = ChannelProvider.getServiceAddress(serviceSign);
        if(inetSocketAddress==null) {
            //通过注册中心获取服务的 套接字，直接通过套接字就能发送请求
            inetSocketAddress = serviceRegistryCenter.lookupService(request.getInterfactName());
            //缓存
            ChannelProvider.putServiceAddress(serviceSign,inetSocketAddress);
        }else {
            logger.info("the service is found in cache");
        }
        //ChannelProvider
        Channel channel = ChannelProvider.get(inetSocketAddress,serializer,clienthandler);
        if(!channel.isActive()){
            group.shutdownGracefully();
            logger.error("服务端通道未打开");
            return null;
        }
        return channel;
    }

    @Deprecated
    @Override
    public Object sendRequest(Channel channel,RpcRequest request) {
        return null;
    }
}
