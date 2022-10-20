package com.geekbrains.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
public class EchoHandler extends SimpleChannelInboundHandler<String>
{
    private static final ConcurrentLinkedDeque<ChannelHandlerContext> clients = new ConcurrentLinkedDeque<>();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) {
        log.debug("Received: {}", s);
        clients.forEach(context -> context.writeAndFlush(s));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.debug("Client connected...");
        clients.add(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.debug("Client disconnected...");
        clients.remove(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("", cause);
    }
}
