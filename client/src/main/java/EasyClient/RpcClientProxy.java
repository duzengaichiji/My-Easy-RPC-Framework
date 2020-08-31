package EasyClient;

import entity.RpcRequest;
import entity.RpcResponse;
import io.netty.channel.ChannelFuture;
import nettyClient.NettyClient;
import rpcInterfaces.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
        RpcRequest rpcRequest = new RpcRequest(
                method.getDeclaringClass().getName(),
                method.getName(),
                args,
                method.getParameterTypes()
        );
        RpcResponse rpcResponse = null;
        if(client instanceof NettyClient){
            try {
//                CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest);
//                rpcResponse = completableFuture.get();
                return client.sendRequest(rpcRequest);
            }catch (Exception e) {
                System.out.println("方法调用失败:"+e);
                return null;
            }
        }
        if(client instanceof EasyRpcClient){
            rpcResponse = (RpcResponse)client.sendRequest(rpcRequest);
        }
        return rpcResponse.getData();
    }
}
