package optic_fusion1.server;

import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.UUID;

public class Session {
    private final SecureRandom secureRandom = new SecureRandom();

    private final UUID id;
    private final PublicKey publicKey;
    private final byte[] hmacKey;

    public Session(final PublicKey publicKey) {
        this.id = UUID.randomUUID();
        this.publicKey = publicKey;

        this.hmacKey = new byte[1024];
        secureRandom.nextBytes(this.hmacKey);

        Server.sessions.put(this.id.toString(), this);
    }

    public UUID getId() {
        return id;
    }

    public byte[] getHmacKey() {
        return hmacKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
