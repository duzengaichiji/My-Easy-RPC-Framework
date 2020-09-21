package com.nhx.core.handlers.consumerhandlers;

import io.netty.channel.ChannelHandler;

public interface CommonClientHandler extends ChannelHandler {
    int getCode();

    static CommonClientHandler getByCode(int code) {
        switch (code) {
            case 0:
                return new SyncClientHandler();
            case 1:
                return new FutureClientHandler();
            case 2:
                return new OneWayClientHandler();
            case 3:
                return new CallbackClientHandler();
            default:
                return null;
        }
    }
}
