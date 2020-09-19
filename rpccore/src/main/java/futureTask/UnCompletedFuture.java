package futureTask;

import enumeration.RpcError;
import exception.RpcException;
import org.apache.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class UnCompletedFuture<T> {
    private static Logger logger = Logger.getLogger(UnCompletedFuture.class.getClass());
    private CompletableFuture<T> rpcResponseCompletableFuture;
    private Integer watiTime;
    private Object resultObject;

    public UnCompletedFuture(CompletableFuture<T> rpcResponseCompletableFuture, Integer watiTime) {
        this.rpcResponseCompletableFuture = rpcResponseCompletableFuture;
        this.watiTime = watiTime;
        this.resultObject = null;
    }

    public Object getResultObject() {
        return resultObject;
    }

    public Object complete(){
        T rpcResponse = null;
        try {
            rpcResponse = rpcResponseCompletableFuture.get(watiTime, TimeUnit.SECONDS);
        }catch (Exception e){
            e.printStackTrace();
            throw new RpcException(RpcError.SERVICE_FAIL);
        }
        this.resultObject = rpcResponse;
        logger.info("mission completed!!!");
        return rpcResponse;
    }
}
