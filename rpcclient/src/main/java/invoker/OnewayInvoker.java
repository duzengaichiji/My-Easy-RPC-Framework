package invoker;

import entity.RpcRequest;
import enumeration.RpcError;
import exception.RpcException;
import io.netty.channel.Channel;
import client.RpcClient;

//不接收结果，只负责发送
public class OnewayInvoker implements Invoker{
    private RpcClient rpcClient;

    public OnewayInvoker(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    @Override
    public Object invoke(Channel channel, RpcRequest request) {
        try {
            //将future结果存入未完成列表，在需要的地方取出
            channel.writeAndFlush(request).addListener(future1->{
                if(future1.isSuccess()){
                    System.out.println("客户端发送消息:"+ request.toString());
                }else{
                    System.out.println("发送消息时有错误:"+future1.cause());
                }
            });
        }catch (Exception e){
            Thread.currentThread().interrupt();
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
        return null;
    }
}
