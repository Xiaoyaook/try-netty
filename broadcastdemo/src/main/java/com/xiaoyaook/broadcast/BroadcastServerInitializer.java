package com.xiaoyaook.broadcast;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

/**
 * created by xiaoyaook on 18-9-23
 *
 * ChannelInitializer 是一个特殊的处理器，来帮助用户配置一个新的 Channel
 */
public class BroadcastServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {

        final ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(new HttpServerCodec()) // 内置的HTTP编解码器
                .addLast(new HttpObjectAggregator(65536)) // 将传入的请求部分和内容聚合到一个FullHttpRequest中
                .addLast(new WebSocketServerCompressionHandler())  // 处理 websocket 的 compression extensions
                .addLast(new WebSocketServerProtocolHandler("/", null, true)) // websocket 协议逻辑的主要处理器
                .addLast(new WebSocketMessageHandler());
    }
}
