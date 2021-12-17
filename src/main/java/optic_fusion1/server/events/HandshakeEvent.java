package optic_fusion1.server.events;

import net.lenni0451.asmevents.event.IEvent;
import optic_fusion1.common.protos.HandshakePacket;
import optic_fusion1.common.protos.Packet;
import optic_fusion1.server.ServerChannelHandlerContext;

public record HandshakeEvent(ServerChannelHandlerContext ctx, Packet packet) implements IEvent {
    public HandshakePacket getHandshakeData() {
        return this.packet.getHandshakeData();
    }
}
