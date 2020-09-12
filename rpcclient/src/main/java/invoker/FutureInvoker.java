package invoker;

import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.RpcError;
import exception.RpcException;
import io.netty.channel.Channel;
import nettyClient.NettyClient;
import rpcInterfaces.RpcClient;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FutureInvoker implements Invoker{

    private RpcClient rpcClient;

    public FutureInvoker(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public Object invoke(Channel channel, RpcRequest rpcRequest) {
        int retries = 0;
        RpcResponse rpcResponse = null;
        while (retries <= ((NettyClient) rpcClient).getExecuteRetries()) {
            try {
                CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) rpcClient.sendRequest(channel, rpcRequest);
                //阻塞直到任务rpcResponse完成，这个机制可以参考future，超时会报错
                rpcResponse = completableFuture.get(((NettyClient) rpcClient).getExecuteWaitTime(), TimeUnit.SECONDS);
                break;
            } catch (Exception e) {
                retries += 1;
                //e.printStackTrace();
                System.out.println("服务调用失败，第 " + retries + " 次重试");
            }
        }
        if (rpcResponse == null) {
            throw new RpcException(RpcError.SERVICE_FAIL);
        }
        return rpcResponse.getData();
    }
}
