package invoker;

import entity.RpcRequest;
import io.netty.channel.Channel;
import rpcInterfaces.RpcClient;

import java.lang.reflect.Method;

public class SyncInvoker implements Invoker{
    private RpcClient rpcClient;

    public SyncInvoker(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public Object invoke(Channel channel, RpcRequest request) {
        return null;
    }
}
