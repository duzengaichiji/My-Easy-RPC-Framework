package proxy;

import entity.RpcRequest;
import enumeration.InvokerCode;
import io.netty.channel.Channel;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import client.RpcClient;

import java.lang.reflect.Method;

//用cglib实现动态代理
public class CglibProxy extends AbstractProxy implements MethodInterceptor {

    public CglibProxy(RpcClient client, InvokerCode invoker) {
        super(client, invoker);
    }

    public <T> T getProxy(Class<T> clazz){
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(this);
        enhancer.setInterfaces(new Class[]{clazz});
        T proxy = (T)enhancer.create();
        return proxy;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        RpcRequest rpcRequest = createRequest(method,objects);
        Channel channel = getChannel(rpcRequest,this.getClient());
        Object result = getInvoker().invoke(channel,rpcRequest);
        return result;
    }
}
