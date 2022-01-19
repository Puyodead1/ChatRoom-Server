package optic_fusion1.server.events;

import com.google.protobuf.InvalidProtocolBufferException;
import net.lenni0451.asmevents.event.IEvent;
import optic_fusion1.common.protos.HandshakeRequest;
import optic_fusion1.common.protos.Packet;
import optic_fusion1.server.ServerChannelHandlerContext;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import static optic_fusion1.common.RSAUtils.getPublicKeyFromBytes;

public record HandshakeRequestEvent(ServerChannelHandlerContext serverChannelHandlerContext, Packet packet) implements IEvent {
    public HandshakeRequest getHandshakeRequestData() throws InvalidProtocolBufferException {
        final byte[] packetData = this.packet.getData().toByteArray();
        return HandshakeRequest.parseFrom(packetData);
    }

    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidProtocolBufferException {
        return getPublicKeyFromBytes(this.getHandshakeRequestData().getRsaPublicKey().toByteArray());
    }
}
