package invoker;

import entity.RpcRequest;
import io.netty.channel.Channel;
import rpcInterfaces.RpcClient;

import java.lang.reflect.Method;

public interface Invoker {
    public Object invoke(Channel channel, RpcRequest request);
}
