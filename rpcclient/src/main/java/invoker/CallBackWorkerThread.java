package invoker;

import client.RpcClient;
import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.RpcError;
import exception.RpcException;
import factory.SingleTonFactory;
import futureTask.UnCompletedFuture;
import futureTask.UnProcessedResponse;
import io.netty.channel.Channel;
import nettyClient.NettyClient;

import java.util.concurrent.CompletableFuture;

public class CallBackWorkerThread implements Runnable{
    UnCompletedFuture<?> unCompletedFuture;

    public CallBackWorkerThread(UnCompletedFuture<?> unCompletedFuture) {
        this.unCompletedFuture = unCompletedFuture;
    }

    @Override
    public void run() {
        //对结果的一些其他处理
        this.unCompletedFuture.complete();
    }
}
