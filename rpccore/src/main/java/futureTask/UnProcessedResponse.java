package futureTask;

import entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

//存放半成品的RpcResponse
public class UnProcessedResponse {
    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedFutures = new ConcurrentHashMap<>();

    public void put(String requestId,CompletableFuture<RpcResponse> future){
        unprocessedFutures.put(requestId,future);
    }

    public void remove(String requestId){
        unprocessedFutures.remove(requestId);
    }

    public void complete(RpcResponse rpcResponse){
        CompletableFuture<RpcResponse> future = unprocessedFutures.remove(rpcResponse.getRequestId());
        if(null!=future){
            //这个才是真的阻塞等待任务完成。。
            future.complete(rpcResponse);
        }else {
            throw new IllegalStateException();
        }
    }
}
