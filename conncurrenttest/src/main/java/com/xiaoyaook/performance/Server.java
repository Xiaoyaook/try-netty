package com.xiaoyaook.performance;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

/**
 * created by xiaoyaook on 18-10-4
 *
 * 服务端
 */
public class Server {

    private static final int PORT = 8000;

    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        EventLoopGroup businessGroup = new NioEventLoopGroup(1000); // 这里传入的是线程数，可以通过调节这里的线程数，来改善响应时间

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);


        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new FixedLengthFrameDecoder(Long.BYTES));
                ch.pipeline().addLast(businessGroup, ServerBusinessHandler.INSTANCE); // 为了不阻塞EventLoop线程，我们这里新建一个EventLoopGroup来处理业务逻辑
//                ch.pipeline().addLast(ServerBusinessThreadPoolHandler.INSTANCE);
            }
        });


        bootstrap.bind(PORT).addListener((ChannelFutureListener) future -> System.out.println("bind success in port: " + PORT));
    }
}
