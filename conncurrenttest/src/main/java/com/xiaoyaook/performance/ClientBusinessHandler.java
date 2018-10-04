package com.xiaoyaook.performance;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * created by xiaoyaook on 18-10-4
 */
@ChannelHandler.Sharable
public class ClientBusinessHandler extends SimpleChannelInboundHandler<ByteBuf> {
    public static final ChannelHandler INSTANCE = new ClientBusinessHandler();

    private static AtomicLong beginTime = new AtomicLong(0);
    private static AtomicLong totalResponseTime = new AtomicLong(0);
    private static AtomicInteger totalRequest = new AtomicInteger(0);

    public static final Thread THREAD = new Thread(() -> {
        try {
            while (true) {
                long duration = System.currentTimeMillis() - beginTime.get();
                if (duration != 0) {
                    // 总请求数除以duration × 1000 为qps，总响应时间除以总请求数得平均相应时间
                    System.out.println("qps: " + 1000 * totalRequest.get() / duration + ", " + "avg response time: " + ((float) totalResponseTime.get()) / totalRequest.get());
                    Thread.sleep(2000);
                }
            }

        } catch (InterruptedException ignored) {
        }
    });

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.executor().scheduleAtFixedRate(() -> {

            ByteBuf byteBuf = ctx.alloc().ioBuffer();
            byteBuf.writeLong(System.currentTimeMillis());  // 把当前时间传给服务端，服务端再原封传回来
            ctx.channel().writeAndFlush(byteBuf);           // 然后与数据传回后的时间相减，可得消息来回所耗费的时间

        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        totalResponseTime.addAndGet(System.currentTimeMillis() - msg.readLong());
        totalRequest.incrementAndGet();

        if (beginTime.compareAndSet(0, System.currentTimeMillis())) {
            THREAD.start();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // ignore
    }
}
