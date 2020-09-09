package proxy;

import EasyClient.EasyRpcClient;
import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.RpcError;
import exception.RpcException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import nettyClient.NettyClient;
import rpcInterfaces.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

//理论上客户端没有服务接口的实现类，需要通过动态代理进行目标方法的调用
public class RpcClientProxy implements InvocationHandler {
    private RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }

    //封装rpc请求，通过客户端进行发送
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //要通过服务的名字，获取其对应的分组，然后将其传到服务端
        String serviceName = method.getDeclaringClass().getName();
        String groupId = NettyClient.getServiceGroupMap().get(serviceName);

        System.out.println("调用方法:"+method.getName()+" 所属分组:"+groupId);
        //封装rpc请求请求
        RpcRequest rpcRequest = new RpcRequest(
                UUID.randomUUID().toString(),
                method.getDeclaringClass().getName(),
                method.getName(),
                args,
                method.getParameterTypes(),
                groupId
        );
        RpcResponse rpcResponse = null;
        //如果用的是netty客户端
        if(client instanceof NettyClient){
            int retries = 0;
            Channel channel = null;
            //5次机会尝试连接服务端
            while (channel == null && retries <= ((NettyClient) client).getConnectRetries()) {
                channel = client.getChannel(rpcRequest);
                retries += 1;
                System.out.println("尝试连接第 "+retries+" 次");
                if(retries>1)  TimeUnit.SECONDS.sleep(((NettyClient) client).getConnectWaitTime());
            }
            if (channel == null) {
                //连接服务端通道失败
                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
            }
            //比较蠢的重试方式
            retries = 0;
            while (retries<=((NettyClient) client).getExecuteRetries()) {
                try {
                    CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRequest(channel, rpcRequest);
                    //阻塞直到任务rpcResponse完成，这个机制可以参考future，超时会报错
                    rpcResponse = completableFuture.get(((NettyClient) client).getExecuteWaitTime(), TimeUnit.SECONDS);
                    break;
                }catch (Exception e){
                    retries+=1;
                    //e.printStackTrace();
                    System.out.println("服务调用失败，第 "+retries+" 次重试");
                }
            }
            if(rpcResponse==null){
                throw new RpcException(RpcError.SERVICE_FAIL);
            }
        }
        //如果是socket客户端
        if(client instanceof EasyRpcClient){
            //rpcResponse = (RpcResponse)client.sendRequest(rpcRequest);
        }
        return rpcResponse.getData();
    }
}
