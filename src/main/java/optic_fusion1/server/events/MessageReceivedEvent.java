package optic_fusion1.server.events;

import net.lenni0451.asmevents.event.IEvent;
import optic_fusion1.common.protos.Packet;
import optic_fusion1.server.ServerChannelHandlerContext;

public record MessageReceivedEvent(ServerChannelHandlerContext serverChannelHandlerContext, Packet packet) implements IEvent {

    public ServerChannelHandlerContext getContext() {
        return this.serverChannelHandlerContext;
    }
}
