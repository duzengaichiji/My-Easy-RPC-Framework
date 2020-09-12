package proxy;

import EasyClient.EasyRpcClient;
import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.InvokerCode;
import enumeration.RpcError;
import exception.RpcException;
import invoker.Invoker;
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
//用jdk实现动态代理
public class JDKProxy extends AbstractProxy implements InvocationHandler {

    public JDKProxy(RpcClient client, InvokerCode invoker) {
        super(client, invoker);
    }

    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }

    //封装rpc请求，通过客户端进行发送
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //创建rpc请求
        RpcRequest rpcRequest = createRequest(method,args);
        //获取传输通道
        Channel channel = getChannel(rpcRequest,this.getClient());

        Object result = getInvoker().invoke(channel,rpcRequest);
        return result;
    }
}
