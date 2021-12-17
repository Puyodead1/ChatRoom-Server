package optic_fusion1.server;

import net.lenni0451.asmevents.EventManager;
import net.lenni0451.asmevents.event.EventTarget;
import optic_fusion1.common.protos.ErrorPacket;
import optic_fusion1.common.protos.Packet;
import optic_fusion1.common.protos.ProtocolVersion;
import optic_fusion1.server.events.HandshakeEvent;
import optic_fusion1.server.events.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerEventListener {
    private final Logger LOGGER = LogManager.getLogger(ServerEventListener.class);

    private final Server server;

    public ServerEventListener(final Server server) {
        this.server = server;
    }

    @EventTarget
    public void onMessageReceived(final MessageReceivedEvent event) {
        final Packet.Type packetType = event.packet().getPacketType();

        switch(packetType) {
            case HANDSHAKE -> EventManager.call(new HandshakeEvent(event.ctx(), event.packet()));
            default -> LOGGER.warn(String.format("[Message Receive] Received an unknown packet type: %s", event.packet()));
        }
    }

    @EventTarget
    public void onHandshake(final HandshakeEvent event) {
        final ProtocolVersion protocolVersion = event.packet().getHandshakeData().getProtocolVersion();
        if (!protocolVersion.equals(this.server.PROTOCOL_VERSION)) {
            // Protocol version is unsupported by this server version

            LOGGER.info(String.format("[Handshake] An outdated client tried to connect: client protocol version %s does not match server protocol version %s. Connection refused", protocolVersion.getNumber(), this.server.PROTOCOL_VERSION.getNumber()));

            ErrorPacket.Builder errorPacket = ErrorPacket.newBuilder();
            errorPacket.setErrorType(ErrorPacket.Type.CONNECTION_REFUSED);
            errorPacket.setDescription("The client is using an outdated protocol version. Please update your client.");

            Packet.Builder responsePacket = Packet.newBuilder();
            responsePacket.setPacketType(Packet.Type.ERROR);
            responsePacket.setErrorData(errorPacket);

            event.ctx().sendPacket(responsePacket.build());
        }
    }
}
