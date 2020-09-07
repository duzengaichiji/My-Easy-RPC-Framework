package servers.handlers;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@ChannelHandler.Sharable
public class AcceptorIdleStateTrigger extends ChannelInboundHandlerAdapter {
    //当通道状态产生变化时，可以在这里进行操作
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //空闲状态处理
        //在程序的默认设置中，客户端空闲4秒就会写入一次，所以这里的读空闲（5秒）是不会出现的
        //如果修改client的idlehandler的空闲时间，就可以引发下面那个错误了
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            //读空闲的状态
            if(state==IdleState.READER_IDLE){
                throw new Exception("idle exception");
            }else {
                super.userEventTriggered(ctx,evt);
            }
        }
    }
}
