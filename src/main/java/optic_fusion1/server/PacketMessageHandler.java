package optic_fusion1.server;

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

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        final ServerChannelHandlerContext handler = new ServerChannelHandlerContext(ctx);
        EventManager.call(new MessageReceivedEvent(handler, packet));
        ReferenceCountUtil.release(packet);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
//        final Session session = new Session(ctx);
//        Server.sessions.put(session.getChannelId().asLongText(), session);
//        EventManager.call(new ClientConnectionEvent(session));
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
