package com.nhx.core.handlers.idle;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 *
 * 重连检测狗，当发现当前的链路不稳定关闭之后，进行12次重连
 */
/*
由于重试的过程中会创建新的pipeline，所以需要让该handler变得shareable
即对于多个pipeline可以共享，所以这也是为什么run里面的逻辑必须保证线程安全
//不明??
//如果去掉这个注解，则会导致定时任务尝试重新建立连接时创建新的channel，但是新的channel创建失败了，后续的链就不会执行了
 */
@Sharable
public abstract class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask , ChannelHandlerHolder {
    private static Logger logger = Logger.getLogger(ConnectionWatchdog.class.getClass());
    private final Bootstrap bootstrap;
    private final Timer timer;
    private final InetSocketAddress inetSocketAddress;
    private volatile boolean reconnect = true;
    private int attempts;

    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, InetSocketAddress inetSocketAddress, boolean reconnect, int attempts) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.inetSocketAddress = inetSocketAddress;
        this.reconnect = reconnect;
        this.attempts = attempts;
    }

    /**
     * channel链路每次active的时候，将其连接的次数重新☞ 0
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("当前链路已经激活了，重连尝试次数重新置为0");
        attempts = 0;
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.warn("链接关闭");
        if(reconnect){
            logger.warn("链接关闭，将进行重连");
            if (attempts < 12) {
                attempts++;
                //重连的间隔时间会越来越长
                int timeout = 2 << attempts;
                //定时（一定间隔）去执行run里面的任务
                timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            }
        }
        ctx.fireChannelInactive();
    }


    public void run(Timeout timeout) throws Exception {
        ChannelFuture future;
        //bootstrap已经初始化好了，只需要将handler填入就可以了
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(handlers());
                }
            });
            future = bootstrap.connect(inetSocketAddress);
            //future = bootstrap.connect(post,port);
        }
        //future对象
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture f) throws Exception {
                boolean succeed = f.isSuccess();
                //如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
                if (!succeed) {
                    logger.error("重连失败");
                    f.channel().pipeline().fireChannelInactive();
                }else{
                    logger.info("重连成功");
                }
            }
        });

    }

}