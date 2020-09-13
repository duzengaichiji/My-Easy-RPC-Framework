package futureTask;

import enumeration.RpcError;
import exception.RpcException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UnCompletedFuture<T> {
    private CompletableFuture<T> rpcResponseCompletableFuture;
    private Integer watiTime;

    public UnCompletedFuture(CompletableFuture<T> rpcResponseCompletableFuture, Integer watiTime) {
        this.rpcResponseCompletableFuture = rpcResponseCompletableFuture;
        this.watiTime = watiTime;
    }

    public Object complete(){
        T rpcResponse = null;
        try {
            rpcResponse = rpcResponseCompletableFuture.get(watiTime, TimeUnit.SECONDS);
        }catch (Exception e){
            e.printStackTrace();
            throw new RpcException(RpcError.SERVICE_FAIL);
        }
        return rpcResponse;
    }
}
