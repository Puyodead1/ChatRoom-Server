package optic_fusion1.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import net.lenni0451.asmevents.EventManager;
import optic_fusion1.common.protos.Packet;
import optic_fusion1.server.events.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketMessageHandler extends SimpleChannelInboundHandler<Packet> {
    private static final Logger LOGGER = LogManager.getLogger(PacketMessageHandler.class);

    private volatile Channel channel;
//    private ChannelHandlerContext ctx;

//    public ChannelFuture sendPacket(Packet packet) throws Exception {
//        if(ctx != null) {
//            return ctx.writeAndFlush(packet);
//        } else {
//            throw new Exception("ChannelHandlerContext not initialized");
//        }
//    }
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) {
//        this.ctx = ctx;
//    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        EventManager.call(new MessageReceivedEvent(new ServerChannelHandlerContext(ctx), packet));
        ReferenceCountUtil.release(packet);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        this.channel = ctx.channel();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("Exception caught in package message handler", cause);
        ctx.close();
    }
}
