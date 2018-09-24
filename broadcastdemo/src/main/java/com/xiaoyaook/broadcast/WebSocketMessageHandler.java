package com.xiaoyaook.broadcast;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by xiaoyaook on 18-9-23
 */
public class WebSocketMessageHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketMessageHandler.class);

    private static final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 数据从服务器接收到后调用
     * @param ctx
     * @param frame
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            final String text = ((TextWebSocketFrame) frame).text();
            LOGGER.info("Received text frame {}", text);

            allChannels.stream()
                    .filter(c -> c != ctx.channel())
//                    .forEach(c -> c.writeAndFlush(frame.copy()));
                    .forEach(c -> {
                        frame.retain();
                        c.writeAndFlush(frame.duplicate());
                    });
            //frame.release();
            // 这里不去手动释放，打断点后可以看到最后的引用计数为0，不会报出IllegalReferenceCountException异常
            // 而按照文中的最后一个方法，最后会报异常
        }
    }

    /**
     * 服务器的连接被建立后调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("Adding new channel {} to list of channels", ctx.channel().remoteAddress());
        allChannels.add(ctx.channel());
    }
}
