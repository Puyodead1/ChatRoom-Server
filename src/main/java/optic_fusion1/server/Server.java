package optic_fusion1.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import optic_fusion1.common.protos.Packet;
import optic_fusion1.common.protos.ProtocolVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static optic_fusion1.common.RSAUtils.*;

public class Server implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(Server.class);
    public static final ProtocolVersion PROTOCOL_VERSION = ProtocolVersion.VERSION_1;
    // Key should be channel id as long text
    public static final HashMap<String, Session> sessions = new HashMap<>();
    private boolean isRunning = false;
    private ExecutorService executor = null;
    public final boolean authRequired;
    public final File dataDir;
    public final KeyPair rsaKeyPair;

    public int port;

    public Server(int port, boolean authRequired) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException {
        this.port = port;
        this.authRequired = authRequired;

        this.dataDir = new File(System.getProperty("user.home"), ".chatroom-server");
        if(!this.dataDir.exists()) {
            this.dataDir.mkdir();
        }

        final File rsaPublicKeyPath = new File(this.dataDir, "server.pem");
        final File rsaPrivateKeyPath = new File(this.dataDir, "server.key");

        if(rsaPublicKeyPath.isFile() && rsaPrivateKeyPath.isFile()) {
            // load keys
            LOGGER.info("Loading RSA Key Pair...");
            this.rsaKeyPair = loadRsaKeyPair(rsaPublicKeyPath, rsaPrivateKeyPath);
            LOGGER.info("Loaded RSA Key Pair");
        } else {
            // generate new keys
            LOGGER.info("Generating new RSA Key Pair...");
            this.rsaKeyPair = generateRsaKeyPair();

            // save keys
            LOGGER.info("Saving RSA Key Pair...");
            saveRsaKeyPair(this.rsaKeyPair, rsaPublicKeyPath, rsaPrivateKeyPath);
            LOGGER.info(String.format("RSA Public Key File: %s", rsaPublicKeyPath));
            LOGGER.info(String.format("RSA Private Key File: %s", rsaPrivateKeyPath));
        }

        LOGGER.info("Data Directory: %s".formatted(this.dataDir.toString()));
    }

    public synchronized void start() {
        if(!isRunning) {
            executor = Executors.newFixedThreadPool(1);
            executor.execute(this);
            isRunning = true;
        }
    }

    public synchronized boolean stop() {
        LOGGER.info("Shutting down");
        boolean bReturn = true;
        if(isRunning) {
            if(executor != null) {
                executor.shutdown();
                try {
                    executor.shutdownNow();
                    if(executor.awaitTermination(calcTime(10, 0.66667), TimeUnit.SECONDS)) {
                        if(!executor.awaitTermination(calcTime(10, 0.33334), TimeUnit.SECONDS)) {
                            bReturn = false;
                        }
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                } finally {
                    executor = null;
                }
            }
            isRunning = false;
        }
        return bReturn;
    }

    private long calcTime(int nTime, double dValue) {
        return (long) ((double) nTime * dValue);
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline p = socketChannel.pipeline();

                    p.addLast(new ProtobufVarint32FrameDecoder());
                    p.addLast(new ProtobufDecoder(Packet.getDefaultInstance()));
                    p.addLast(new ProtobufVarint32LengthFieldPrepender());
                    p.addLast(new ProtobufEncoder());
                    p.addLast(new PacketMessageHandler());
                }
            });
            b.option(ChannelOption.SO_BACKLOG, 128);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(this.port);
            LOGGER.info(String.format("ChatRoom server listening on port %s", this.port));
            f.sync().channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.fatal(String.format("Failed to start ChatRoom server: %s", e.getLocalizedMessage()));
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
