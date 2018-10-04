package com.xiaoyaook.performance;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

/**
 * created by xiaoyaook on 18-10-4
 *
 * 客户端
 */
public class Client {

    private static final String SERVER_HOST = "127.0.0.1";

    private static final int PORT = 8000;

    public static void main(String[] args) throws Exception {
        new Client().start(PORT);
    }

    public void start(int port) throws Exception {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new FixedLengthFrameDecoder(Long.BYTES));
                ch.pipeline().addLast(ClientBusinessHandler.INSTANCE);
            }
        });

        for (int i = 0; i < 10000; i++) {  // 这里设置我们的最大并发量
            bootstrap.connect(SERVER_HOST, port).get();
        }
    }
}
