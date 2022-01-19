package optic_fusion1.server.events;

import com.google.protobuf.InvalidProtocolBufferException;
import net.lenni0451.asmevents.event.IEvent;
import optic_fusion1.common.protos.AuthenticationRequestPacket;
import optic_fusion1.common.protos.HandshakeRequest;
import optic_fusion1.common.protos.Packet;
import optic_fusion1.server.Server;
import optic_fusion1.server.ServerChannelHandlerContext;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import static optic_fusion1.common.RSAUtils.getPublicKeyFromBytes;

public record AuthenticationRequestEvent(Server server, ServerChannelHandlerContext serverChannelHandlerContext, Packet packet) implements IEvent {

    public AuthenticationRequestPacket getRequestData() throws InvalidProtocolBufferException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        final byte[] encryptedPacketData = this.packet.getData().toByteArray();
        // Decrypt the data
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, server.rsaKeyPair.getPrivate());
        final byte[] packetData = cipher.doFinal(encryptedPacketData);
        return AuthenticationRequestPacket.parseFrom(packetData);
    }
}
