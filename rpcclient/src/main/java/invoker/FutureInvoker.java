package invoker;

import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.RpcError;
import exception.RpcException;
import factory.SingleTonFactory;
import io.netty.channel.Channel;
import nettyClient.NettyClient;
import futureTask.UnCompletedFuture;
import futureTask.UnProcessedResponse;
import client.RpcClient;

import java.util.concurrent.CompletableFuture;

public class FutureInvoker implements Invoker{

    private RpcClient rpcClient;
    private UnProcessedResponse unProcessedResponse;

    public FutureInvoker(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        this.unProcessedResponse = SingleTonFactory.getInstance(UnProcessedResponse.class);
    }

    //发送请求
    public CompletableFuture<RpcResponse> sendRequest(Channel channel, RpcRequest rpcRequest){
        //用future接收结果的好处就是，可以在任何需要的时候去获取，而不是必须在当前就阻塞的等待服务端对请求的处理
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            //将future结果存入未完成列表，在需要的地方取出
            unProcessedResponse.put(rpcRequest.getRequestId(),resultFuture);
            channel.writeAndFlush(rpcRequest).addListener(future1->{
                if(future1.isSuccess()){
                    System.out.println("客户端发送消息:"+ rpcRequest.toString());
                }else{
                    resultFuture.completeExceptionally(future1.cause());
                    System.out.println("发送消息时有错误:"+future1.cause());
                }
            });
        }catch (Exception e){
            Thread.currentThread().interrupt();
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        return resultFuture;
    }

    @Override
    public UnCompletedFuture invoke(Channel channel, RpcRequest rpcRequest) {
        //多次重试
        int retries = 0;
        CompletableFuture<RpcResponse> completableFuture = null;
        while (retries <= ((NettyClient) rpcClient).getExecuteRetries()) {
            try {
                completableFuture = sendRequest(channel, rpcRequest);
                //阻塞直到任务rpcResponse完成，这个机制可以参考future，超时会报错
                break;
            } catch (Exception e) {
                retries += 1;
                //e.printStackTrace();
                System.out.println("服务调用失败，第 " + retries + " 次重试");
            }
        }
        return new UnCompletedFuture<RpcResponse>(completableFuture,((NettyClient) rpcClient).getExecuteWaitTime());
    }
}
