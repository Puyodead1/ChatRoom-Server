package optic_fusion1.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import optic_fusion1.server.protos.Packet;

public class PacketMessageHandler extends SimpleChannelInboundHandler<Packet> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        System.out.println(msg.toString());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
