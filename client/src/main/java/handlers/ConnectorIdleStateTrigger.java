package handlers;

import entity.HeartbeatRequest;
import entity.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.util.UUID;

@ChannelHandler.Sharable
public class ConnectorIdleStateTrigger extends ChannelInboundHandlerAdapter {
//    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat",
//            CharsetUtil.UTF_8));
    //private static final HeartbeatRequest heartbeatRequest = new HeartbeatRequest(UUID.randomUUID().toString());
    //鉴于对编码解码器了解不足，暂时用这个代替心跳包
    private static final RpcRequest heartbeatRequest = new RpcRequest("heartBeat","fuck","fuck",null,null,"group1");

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            //检测到写空闲状态，触发写入操作
            if (state == IdleState.WRITER_IDLE) {
                // write heartbeat to server
                //不加channel的话只能write ByteBuf吧。。
                //ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
                //所以要写入自定义对象的话应该使用channel
                ctx.channel().writeAndFlush(heartbeatRequest);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
