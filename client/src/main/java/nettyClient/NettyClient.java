package nettyClient;

import codec.CommonDecoder;
import codec.CommonEncoder;
import entity.RpcRequest;
import entity.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import rpcInterfaces.RpcClient;
import serializer.JsonSerializer;

public class NettyClient implements RpcClient {

    private String host;
    private int port;
    private static Bootstrap bootstrap;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new CommonDecoder())
                                .addLast(new CommonEncoder(new JsonSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });
    }

    @Override
    public Object sendRequest(final RpcRequest request) {
        try{
            ChannelFuture future = bootstrap.connect(host,port).sync();
            System.out.println("客户端连接到服务器"+host+":"+port);
            Channel channel = future.channel();
            if(channel!=null){
                channel.writeAndFlush(request).addListener(future1->{
                    if(future1.isSuccess()){
                        System.out.println("客户端发送消息"+ request.toString());
                    }else{
                        System.out.println("发送消息时有错误"+future1.cause());
                    }
                });
                channel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                RpcResponse rpcResponse = channel.attr(key).get();
                return rpcResponse.getData();
            }
        }catch (InterruptedException e){
            System.out.println(e);
        }
        return null;
    }
}
