package proxy;

import entity.RpcRequest;
import enumeration.InvokerCode;
import enumeration.RpcError;
import exception.RpcException;
import invoker.*;
import io.netty.channel.Channel;
import nettyClient.NettyClient;
import rpcInterfaces.RpcClient;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class AbstractProxy {
    //代理类里面需要一个client负责发送消息
    private RpcClient client;
    //根据不同场合，选取不同的invoker类型
    private Invoker invoker;

    public AbstractProxy(RpcClient client, InvokerCode invoker) {
        this.client = client;
        switch (invoker){
            case SYNC_INVOKER:
                this.invoker = new SyncInvoker(client);
                break;
            case FUTURE_INVOKER:
                this.invoker = new FutureInvoker(client);
                break;
            case ONEWAY_INVOKER:
                this.invoker = new OnewayInvoker(client);
                break;
            case CALLBACK_INVOKER:
                this.invoker = new CallbackInvoker(client);
                break;
            default:
                throw new RpcException(RpcError.NO_SUCH_INVOKER);
        }
    }

    public RpcClient getClient() {
        return client;
    }

    public void setClient(RpcClient client) {
        this.client = client;
    }

    public Invoker getInvoker() {
        return invoker;
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public RpcRequest createRequest(Method method, Object[] args){
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
        return rpcRequest;
    }

    public Channel getChannel(RpcRequest rpcRequest, RpcClient client) throws InterruptedException {
        int retries = 0;
        Channel channel = null;
        //5次机会尝试连接服务端
        while (channel == null && retries <= ((NettyClient) client).getConnectRetries()) {
            channel = client.getChannel(rpcRequest);
            retries += 1;
            System.out.println("尝试连接第 " + retries + " 次");
            if (retries > 1) TimeUnit.SECONDS.sleep(((NettyClient) client).getConnectWaitTime());
        }
        if (channel == null) {
            //连接服务端通道失败
            throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
        }
        return channel;
    }
}
