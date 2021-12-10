package optic_fusion1.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatRoomServer {

    private static final Logger LOGGER = LogManager.getLogger(ChatRoomServer.class);

    public static void main(String[] args) {

        OptionParser optionParser = new OptionParser();
        OptionSpec<Void> helpSpec = optionParser.accepts("help").forHelp();
        OptionSpec<Integer> portSpec = optionParser.accepts("port", "Server port").withOptionalArg().ofType(Integer.class).defaultsTo(8888);

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        try {
            OptionSet optionSet = optionParser.parse(args);
            if (optionSet.has(helpSpec)) {
                optionParser.printHelpOn(System.out);
                return;
            }

            int serverPort = optionSet.valueOf(portSpec);

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(serverPort);
            LOGGER.info(String.format("ChatRoom server listening on port %s", serverPort));
            f.sync();

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.fatal(String.format("Failed to start ChatRoom server: %s", e.getLocalizedMessage()));
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
