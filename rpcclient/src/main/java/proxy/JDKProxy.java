package proxy;

import entity.RpcRequest;
import enumeration.InvokerCode;
import io.netty.channel.Channel;
import client.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
