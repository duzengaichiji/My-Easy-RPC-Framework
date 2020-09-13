package client;

import entity.RpcRequest;
import handlers.CommonClientHandler;
import io.netty.channel.Channel;

public interface RpcClient {
    Channel getChannel(RpcRequest request, CommonClientHandler clientHandler);
    Object sendRequest(Channel channel,RpcRequest request);
    void setServiceGroup(Class service,String groupId);
}
