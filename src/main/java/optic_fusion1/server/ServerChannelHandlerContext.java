package optic_fusion1.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import optic_fusion1.common.protos.Packet;

public record ServerChannelHandlerContext(ChannelHandlerContext ctx) {

    public ChannelFuture sendPacket(Packet packet) {
        return ctx.writeAndFlush(packet);
    }
}
