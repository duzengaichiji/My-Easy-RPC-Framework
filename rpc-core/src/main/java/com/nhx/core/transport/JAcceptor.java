package com.nhx.core.transport;

import java.net.SocketAddress;

/**
 * @ClassName nhx
 * @Author nhx
 * @Date 2020/9/22 20:16
 **/
public interface JAcceptor extends Transporter {
    /**
     * 地址
     *
     * @return
     */
    SocketAddress localAddress();

    /**
     * 绑定端口
     *
     * @return
     */
    int boundPort();

    /**
     * 开启服务
     *
     * @throws InterruptedException
     */
    void start() throws InterruptedException;

    void start(boolean sync) throws InterruptedException;

    /**
     * 关闭服务
     */
    void shutdownGracefully();
}
