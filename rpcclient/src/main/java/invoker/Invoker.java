package invoker;

import entity.RpcRequest;
import io.netty.channel.Channel;

public interface Invoker {
    Object invoke(Channel channel, RpcRequest request);
}
