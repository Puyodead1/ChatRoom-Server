package optic_fusion1.server.events;

import net.lenni0451.asmevents.event.IEvent;
import optic_fusion1.common.protos.HandshakeRequest;
import optic_fusion1.common.protos.Packet;
import optic_fusion1.server.ServerChannelHandlerContext;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import static optic_fusion1.common.RSAUtils.getPublicKeyFromBytes;

public record HandshakeRequestEvent(ServerChannelHandlerContext serverChannelHandlerContext, Packet packet) implements IEvent {
    public HandshakeRequest getHandshakeRequestData() {
        return this.packet.getHandshakeRequestData();
    }

    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getPublicKeyFromBytes(this.getHandshakeRequestData().getRsaPublicKey().toByteArray());
    }
}
