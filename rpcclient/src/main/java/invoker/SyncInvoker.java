package invoker;

import entity.RpcRequest;
import entity.RpcResponse;
import enumeration.RpcError;
import exception.RpcException;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import client.RpcClient;

import java.util.concurrent.TimeUnit;

public class SyncInvoker implements Invoker{
    private RpcClient rpcClient;

    public SyncInvoker(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }



    @Override
    public Object invoke(Channel channel, RpcRequest request) {
        //这种是同步等待的方式，需要阻塞等待服务端送回结果
        channel.writeAndFlush(request).addListener(future1->{
            if(future1.isSuccess()){
                System.out.println("客户端发送消息:"+ request.toString());
            }else{
                System.out.println("发送消息时有错误:"+future1.cause());
            }
        });
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RpcResponse rpcResponse = null;
        try {
            //channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            rpcResponse = channel.attr(key).get();
            return rpcResponse.getData();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RpcException(RpcError.SERVICE_FAIL);
        }
    }
}
