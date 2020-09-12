package nettyClient;

import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.RpcError;
import exception.RpcException;
import factory.SingleTonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import registry.NacosServiceRegistryCenter;
import registry.ServiceRegistryCenter;
import results.UnProcessedResponse;
import rpcInterfaces.AbstractRpcClient;
import serializer.CommonSerializer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class NettyClient extends AbstractRpcClient {

    private final static Bootstrap bootstrap;
    private final static EventLoopGroup group;
    private UnProcessedResponse unProcessedResponse;
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

    public NettyClient(Integer serializer){
        this.serviceRegistryCenter = new NacosServiceRegistryCenter();
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unProcessedResponse = SingleTonFactory.getInstance(UnProcessedResponse.class);
    }

    public NettyClient(Integer serializer,int connectRetries,int connectWaitTime){
        this(serializer);
        this.setConnectRetries(connectRetries);
        this.setConnectWaitTime(connectWaitTime);
    }

    public NettyClient(Integer serializer,int connectRetries,int connectWaitTime,int executeRetries,int executeWaitTime){
        this(serializer);
        this.setExecuteRetries(executeRetries);
        this.setExecuteWaitTime(executeWaitTime);
        this.setConnectWaitTime(connectWaitTime);
        this.setConnectRetries(connectRetries);
    }

    public static Map<String, String> getServiceGroupMap() {
        return serviceGroupMap;
    }

    public static void setServiceGroupMap(Map<String, String> serviceGroupMap) {
        NettyClient.serviceGroupMap = serviceGroupMap;
    }

    //分离连接通道的方法和发送请求的方法，以便多次重试
    @Override
    public Channel getChannel(RpcRequest request) {
        if(serializer==null){
            System.out.println("未设置序列化器");
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
            System.out.println("the service is found in cache");
        }
        //ChannelProvider
        Channel channel = ChannelProvider.get(inetSocketAddress,serializer);
        if(!channel.isActive()){
            group.shutdownGracefully();
            System.out.println("服务端通道未打开");
            return null;
        }
        return channel;
    }

    //将结果封装进CompletableFuture，异步的处理请求结果
    @Override
    public CompletableFuture<RpcResponse> sendRequest(Channel channel,RpcRequest request) {
        //用future接收结果的好处就是，可以在任何需要的时候去获取，而不是必须在当前就阻塞的等待服务端对请求的处理
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            //将future结果存入未完成列表，在需要的地方取出
            unProcessedResponse.put(request.getRequestId(),resultFuture);
            channel.writeAndFlush(request).addListener(future1->{
                if(future1.isSuccess()){
                    System.out.println("客户端发送消息:"+ request.toString());
                }else{
                    resultFuture.completeExceptionally(future1.cause());
                    System.out.println("发送消息时有错误:"+future1.cause());
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

    @Override
    public void setServiceGroup(Class service, String groupId) {
        if(serviceGroupMap.containsKey(service.getCanonicalName())){
            serviceGroupMap.remove(service.getCanonicalName());
        }
        serviceGroupMap.put(service.getCanonicalName(),groupId);
    }

    public static void main(String[] args) {
//        RpcClient client = new NettyClient(SerializerCode.KRYO.getCode());
//        //setting service groups
//        client.setServiceGroup(HelloService.class,"group1");
//        JDKProxy rpcClientProxy = new JDKProxy(client);
//        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
//        HelloObject object = new HelloObject(12,"netty message");
//        String res = helloService.hello(object);
//        System.out.println(res);
    }
}
