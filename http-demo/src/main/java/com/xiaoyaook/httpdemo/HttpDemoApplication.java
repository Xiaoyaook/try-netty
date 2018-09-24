package com.xiaoyaook.httpdemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 实现CommandLineRunner 接口的 Component 会在所有 Spring Beans 都初始化之后，
 * SpringApplication.run() 之前执行，非常适合在应用程序启动之初进行一些数据初始化的工作。
 */
@SpringBootApplication
public class HttpDemoApplication implements CommandLineRunner {

    private int port = 8888;
    private static final int KB = 1024;
    @Autowired
    private AppInitializer appInitializer;

    public static void main(String[] args) {
        SpringApplication.run(HttpDemoApplication.class, args);
    }

    @Override
    public void run(final String... args) {
        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        final EventLoopGroup master = new NioEventLoopGroup();
        final EventLoopGroup worker = new NioEventLoopGroup();
        try {
            serverBootstrap
                    .option(ChannelOption.SO_BACKLOG, 4 * KB)
                    // ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数，
                    // 函数listen(int socketfd,int backlog)用来初始化服务端可连接队列，
                    // 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，
                    // 多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，
                    // backlog参数指定了队列的大小
                    .option(ChannelOption.TCP_NODELAY, true) // 关闭Nagle算法
                    .group(master, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(appInitializer);
            final Channel ch = serverBootstrap.bind(port).sync().channel();
            System.out.println("start app ok...");
            ch.closeFuture().sync();
        } catch (final InterruptedException e) {
            //ignore
        } finally {
            master.shutdownGracefully();
            worker.shutdownGracefully();
            System.out.println("stop app ok...");
        }
    }
}
