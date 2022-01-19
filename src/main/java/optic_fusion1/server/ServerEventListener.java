package optic_fusion1.server;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import net.lenni0451.asmevents.EventManager;
import net.lenni0451.asmevents.event.EventTarget;
import optic_fusion1.common.protos.ErrorPacket;
import optic_fusion1.common.protos.HandshakeResponse;
import optic_fusion1.common.protos.Packet;
import optic_fusion1.common.protos.ProtocolVersion;
import optic_fusion1.server.events.AuthenticationRequestEvent;
import optic_fusion1.server.events.ClientConnectionEvent;
import optic_fusion1.server.events.HandshakeRequestEvent;
import optic_fusion1.server.events.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static optic_fusion1.common.ChatRoomUtils.makeErrorPacket;

public class ServerEventListener {
    private final Logger LOGGER = LogManager.getLogger(ServerEventListener.class);
    private final Server server;

    public ServerEventListener(final Server server) {
        this.server = server;
    }

    @EventTarget
    public void onClientConnection(final ClientConnectionEvent event) {
        LOGGER.info(String.format("[Client Connection] Client %s connected from %s", event.getChannelId().asShortText(), event.getChannel().remoteAddress().toString()));
    }

    @EventTarget
    public void onMessageReceived(final MessageReceivedEvent event) {
        final Packet.Type packetType = event.packet().getPacketType();

        // Check for missing signature on packets
        if (packetType != Packet.Type.HANDSHAKE_REQUEST) {
            Packet packet = event.packet();
            if (!packet.hasSignature()) {
                // The signature field is required on all messages other than handshake requests and responses, send a validation error
                LOGGER.error("[MessageReceived] [ValidationError] Packet is missing signature!");

                final ErrorPacket.Builder errorPacket = ErrorPacket.newBuilder();
                errorPacket.setErrorType(ErrorPacket.Type.VALIDATION);
                errorPacket.setDescription("The signature field is required, but was missing.");

                final Packet.Builder newPacket = Packet.newBuilder();
                newPacket.setPacketType(Packet.Type.ERROR);
                newPacket.setData(errorPacket.build().toByteString());

                event.serverChannelHandlerContext().sendPacket(newPacket.build());
                return;
            }

            // TODO: validate session id and get session
        }

        switch (packetType) {
            case HANDSHAKE_REQUEST -> EventManager.call(new HandshakeRequestEvent(event.serverChannelHandlerContext(), event.packet()));
            case AUTHENTICATION_REQUEST -> EventManager.call(new AuthenticationRequestEvent(server, event.serverChannelHandlerContext(), event.packet()));
            default -> LOGGER.warn(String.format("[Message Receive] Received an unknown packet type: %s", event.packet()));
        }
    }

    @EventTarget
    public void onHandshakeRequest(final HandshakeRequestEvent event) {
        try {
            final Session session = new Session(event.getPublicKey());

            final ProtocolVersion protocolVersion = event.getHandshakeRequestData().getProtocolVersion();
            if (!protocolVersion.equals(Server.PROTOCOL_VERSION)) {
                // Protocol version unsupported, send error packet

                LOGGER.info(String.format("[Handshake] An outdated client tried to connect: client protocol version %s does not match server protocol version %s. Connection refused", protocolVersion.getNumber(), Server.PROTOCOL_VERSION.getNumber()));

                final Packet packet = makeErrorPacket(ErrorPacket.Type.UNKNOWN, String.format("Outdated client! Server is using protocol version %s while client is using protocol version %s, please update your client to the latest version.", Server.PROTOCOL_VERSION.getNumber(), protocolVersion.getNumber()));

                event.serverChannelHandlerContext().sendPacket(packet);
                return;
            }

            LOGGER.info(String.format("[HandshakeRequest] Session ID: %s", session.getId()));

            // Create response packet
            final HandshakeResponse.Builder handshakeResponsePacket = HandshakeResponse.newBuilder();
            handshakeResponsePacket.setProtocolVersion(Server.PROTOCOL_VERSION);
            handshakeResponsePacket.setSessionId(session.getId().toString());
            handshakeResponsePacket.setRsaPublicKey(ByteString.copyFrom(this.server.rsaKeyPair.getPublic().getEncoded()));
            handshakeResponsePacket.setHmacKey(ByteString.copyFrom(session.getHmacKey()));
            handshakeResponsePacket.setAuthenticationRequired(this.server.authRequired);


            final Packet.Builder packet = Packet.newBuilder();
            packet.setPacketType(Packet.Type.HANDSHAKE_RESPONSE);
            packet.setData(handshakeResponsePacket.build().toByteString());

            event.serverChannelHandlerContext().sendPacket(packet.build());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            // Error creating session, send error packet
            LOGGER.error(String.format("Exception while creating session: %s", ex.getLocalizedMessage()));

            final Packet packet = makeErrorPacket(ErrorPacket.Type.SESSION, ex.getLocalizedMessage());

            event.serverChannelHandlerContext().sendPacket(packet);
        } catch (InvalidProtocolBufferException ex) {
            // something went wrong trying to reassemble the packet?
            LOGGER.error(String.format("[HandshakeRequest] Exception caught: %s", ex.getLocalizedMessage()));

            final Packet packet = makeErrorPacket(ErrorPacket.Type.UNKNOWN, ex.getLocalizedMessage());

            event.serverChannelHandlerContext().sendPacket(packet);
        }
    }

    @EventTarget
    public void onAuthenticationRequest(final AuthenticationRequestEvent event) {
        try {
            LOGGER.info(String.format("[AuthenticationRequest] Got authentication request for session: %s; username: %s; password: %s", event.getRequestData().getSessionId(), event.getRequestData().getUsername(), event.getRequestData().getPassword()));
        } catch (InvalidKeyException | InvalidProtocolBufferException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException ex) {
            LOGGER.error(String.format("[AuthenticationRequest] Exception caught while trying to parse authentication packet: %s", ex.getLocalizedMessage()));
        }
    }
}
