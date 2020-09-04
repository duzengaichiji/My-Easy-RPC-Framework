package rpcInterfaces;

import entity.RpcRequest;

public interface RpcClient {
    Object sendRequest(RpcRequest request);
    void setServiceGroup(Class service,String groupId);
}
