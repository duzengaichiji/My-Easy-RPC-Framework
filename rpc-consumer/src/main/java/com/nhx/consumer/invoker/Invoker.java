package com.nhx.consumer.invoker;

import com.nhx.core.entity.RpcRequest;
import io.netty.channel.Channel;

public interface Invoker {
    Object invoke(Channel channel, RpcRequest request);
}
