package optic_fusion1.server;

import com.google.protobuf.ByteString;
import net.lenni0451.asmevents.EventManager;
import net.lenni0451.asmevents.event.EventTarget;
import optic_fusion1.common.protos.ErrorPacket;
import optic_fusion1.common.protos.HandshakeResponse;
import optic_fusion1.common.protos.Packet;
import optic_fusion1.common.protos.ProtocolVersion;
import optic_fusion1.server.events.ClientConnectionEvent;
import optic_fusion1.server.events.HandshakeRequestEvent;
import optic_fusion1.server.events.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

import static optic_fusion1.common.RSAUtils.verifySignature;

public class ServerEventListener {
    private final Logger LOGGER = LogManager.getLogger(ServerEventListener.class);
    private final Server server;

    public ServerEventListener(final Server server) {
        this.server = server;
    }

    @EventTarget
    public void onClientConnection(final ClientConnectionEvent event) {
        LOGGER.info(String.format("[Client Connection] Client %s connected from %s",  event.getChannelId().asShortText(), event.getChannel().remoteAddress().toString()));
    }

    @EventTarget
    public void onMessageReceived(final MessageReceivedEvent event) {
        final Packet.Type packetType = event.packet().getPacketType();

        // Check for missing signature on packets
        if(packetType != Packet.Type.HANDSHAKE_REQUEST) {
            Packet packet = event.packet();
            if(!packet.hasSignature()) {
                // The signature field is required on all messages other than handshake requests and responses, send a validation error
                LOGGER.error("[MessageReceived] [ValidationError] Packet is missing signature!");
                ErrorPacket.Builder errorPacket = ErrorPacket.newBuilder();
                errorPacket.setErrorType(ErrorPacket.Type.VALIDATION);
                errorPacket.setDescription("The signature field is required, but was missing.");

                Packet.Builder newPacket = Packet.newBuilder();
                newPacket.setPacketType(Packet.Type.ERROR);
                newPacket.setErrorData(errorPacket.build());

                event.serverChannelHandlerContext().sendPacket(newPacket.build());
                return;
            }
        }

        switch(packetType) {
            case HANDSHAKE_REQUEST -> EventManager.call(new HandshakeRequestEvent(event.serverChannelHandlerContext(), event.packet()));
            default -> LOGGER.warn(String.format("[Message Receive] Received an unknown packet type: %s", event.packet()));
        }
    }

    @EventTarget
    public void onHandshakeRequest(final HandshakeRequestEvent event) {
        try {
            final Session session = new Session(event.getPublicKey());

            final ProtocolVersion protocolVersion = event.packet().getHandshakeRequestData().getProtocolVersion();
            if (!protocolVersion.equals(Server.PROTOCOL_VERSION)) {
                // Protocol version unsupported, send error packet

                LOGGER.info(String.format("[Handshake] An outdated client tried to connect: client protocol version %s does not match server protocol version %s. Connection refused", protocolVersion.getNumber(), Server.PROTOCOL_VERSION.getNumber()));

                ErrorPacket.Builder errorPacket = ErrorPacket.newBuilder();
                errorPacket.setErrorType(ErrorPacket.Type.CONNECTION_REFUSED);
                errorPacket.setDescription(String.format("Outdated client! Server is using protocol version %s while client is using protocol version %s, please update your client to the latest version.", Server.PROTOCOL_VERSION.getNumber(), protocolVersion.getNumber()));

                Packet.Builder packet = Packet.newBuilder();
                packet.setPacketType(Packet.Type.ERROR);
                packet.setErrorData(errorPacket);

                event.serverChannelHandlerContext().sendPacket(packet.build());
                return;
            }

            LOGGER.info(String.format("[HandshakeRequest] Session ID: %s", session.getId()));

            // Create response packet
            HandshakeResponse.Builder handshakeResponsePacket = HandshakeResponse.newBuilder();
            handshakeResponsePacket.setProtocolVersion(Server.PROTOCOL_VERSION);
            handshakeResponsePacket.setSessionId(session.getId().toString());
            handshakeResponsePacket.setRsaPublicKey(ByteString.copyFrom(this.server.rsaKeyPair.getPublic().getEncoded()));
            handshakeResponsePacket.setHmacKey(ByteString.copyFrom(session.getHmacKey()));


            Packet.Builder packet = Packet.newBuilder();
            packet.setPacketType(Packet.Type.HANDSHAKE_RESPONSE);
            packet.setHandshakeResponseData(handshakeResponsePacket.build());

            event.serverChannelHandlerContext().sendPacket(packet.build());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            // Error creating session, send error packet
            LOGGER.error(String.format("Exception while creating session: %s", ex.getLocalizedMessage()));

            ErrorPacket.Builder errorPacket = ErrorPacket.newBuilder();
            errorPacket.setErrorType(ErrorPacket.Type.SESSION);
            errorPacket.setDescription(ex.getLocalizedMessage());

            Packet.Builder packet = Packet.newBuilder();
            packet.setPacketType(Packet.Type.ERROR);
            packet.setErrorData(errorPacket);

            event.serverChannelHandlerContext().sendPacket(packet.build());
        }
    }
}
