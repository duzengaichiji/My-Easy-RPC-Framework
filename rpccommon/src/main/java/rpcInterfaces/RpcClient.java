package rpcInterfaces;

import entity.RpcRequest;
import io.netty.channel.Channel;

public interface RpcClient {
    Channel getChannel(RpcRequest request);
    Object sendRequest(Channel channel,RpcRequest request);
    void setServiceGroup(Class service,String groupId);
}
