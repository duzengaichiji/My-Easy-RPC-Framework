package proxy;

import EasyClient.EasyRpcClient;
import entity.RpcRequest;
import entity.RpcResponse;
import io.netty.channel.ChannelFuture;
import nettyClient.NettyClient;
import rpcInterfaces.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
        System.out.println("调用方法:"+method.getName());
        //封装rpc请求请求
        RpcRequest rpcRequest = new RpcRequest(
                UUID.randomUUID().toString(),
                method.getDeclaringClass().getName(),
                method.getName(),
                args,
                method.getParameterTypes()
        );
        RpcResponse rpcResponse = null;
        //如果用的是netty客户端
        if(client instanceof NettyClient){
            try {
                CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest);
                //阻塞直到任务rpcResponse完成，这个机制可以参考future
                rpcResponse = completableFuture.get();
                //return client.sendRequest(rpcRequest);
            }catch (Exception e) {
                System.out.println("方法调用失败:"+e);
                return null;
            }
        }
        //如果是socket客户端
        if(client instanceof EasyRpcClient){
            rpcResponse = (RpcResponse)client.sendRequest(rpcRequest);
        }
        return rpcResponse.getData();
    }
}
