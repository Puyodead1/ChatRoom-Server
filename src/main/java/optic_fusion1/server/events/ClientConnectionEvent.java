package optic_fusion1.server.events;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import net.lenni0451.asmevents.event.IEvent;
import optic_fusion1.server.ServerChannelHandlerContext;

public record ClientConnectionEvent(ServerChannelHandlerContext serverChannelHandlerContext) implements IEvent {
    public ChannelHandlerContext getChannelHandlerContext() {
        return this.serverChannelHandlerContext.channelHandlerContext();
    }

    public Channel getChannel() {
        return this.getChannelHandlerContext().channel();
    }

    public ChannelId getChannelId() {
        return this.getChannel().id();
    }
}
