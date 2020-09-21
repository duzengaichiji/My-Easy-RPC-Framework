package com.nhx.consumer.client;

import com.nhx.core.entity.RpcRequest;
import com.nhx.core.handlers.consumerhandlers.CommonClientHandler;
import io.netty.channel.Channel;

public interface RpcClient {
    Channel getChannel(RpcRequest request, CommonClientHandler clientHandler);
    Object sendRequest(Channel channel,RpcRequest request);
    void setServiceGroup(Class service,String groupId);
}
