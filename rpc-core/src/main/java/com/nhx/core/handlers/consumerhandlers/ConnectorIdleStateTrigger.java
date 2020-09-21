package com.nhx.core.handlers.consumerhandlers;

import com.nhx.core.entity.HeartbeatRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.UUID;

@ChannelHandler.Sharable
public class ConnectorIdleStateTrigger extends ChannelInboundHandlerAdapter {
//    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat",
//            CharsetUtil.UTF_8));
    private static final HeartbeatRequest heartbeatRequest = new HeartbeatRequest(UUID.randomUUID().toString());

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
