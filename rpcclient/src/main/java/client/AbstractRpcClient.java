package client;

import entity.RpcRequest;
import handlers.CommonClientHandler;
import io.netty.channel.Channel;

public class AbstractRpcClient implements RpcClient{
    private int connectRetries = 0;
    private int executeRetries = 3;
    private int connectWaitTime = 1;
    private int executeWaitTime = 5;

    public int getConnectRetries() {
        return connectRetries;
    }

    public void setConnectRetries(int connectRetries) {
        this.connectRetries = connectRetries;
    }

    public int getExecuteRetries() {
        return executeRetries;
    }

    public void setExecuteRetries(int executeRetries) {
        this.executeRetries = executeRetries;
    }

    public int getConnectWaitTime() {
        return connectWaitTime;
    }

    public void setConnectWaitTime(int connectWaitTime) {
        this.connectWaitTime = connectWaitTime;
    }

    public int getExecuteWaitTime() {
        return executeWaitTime;
    }

    public void setExecuteWaitTime(int executeWaitTime) {
        this.executeWaitTime = executeWaitTime;
    }

    @Override
    public Channel getChannel(RpcRequest request, CommonClientHandler clienthandler) {
        return null;
    }

    @Override
    public Object sendRequest(Channel channel, RpcRequest request) {
        return null;
    }

    @Override
    public void setServiceGroup(Class service, String groupId) {

    }
}
