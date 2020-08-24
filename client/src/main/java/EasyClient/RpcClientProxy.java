package EasyClient;

import entity.RpcRequest;
import entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

//理论上客户端没有服务接口的实现类，需要通过动态代理进行目标方法的调用
public class RpcClientProxy implements InvocationHandler {
    private String host;
    private int port;

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
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
        EasyRpcClient rpcClient = new EasyRpcClient();
        return ((RpcResponse) rpcClient.sendRequest(rpcRequest,host,port)).getData();
    }

    @Override
    public String toString() {
        return "RpcClientProxy{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
