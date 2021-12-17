package optic_fusion1.server;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.lenni0451.asmevents.EventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.AnsiConsole;

public class ChatRoomServer {

    private static final Logger LOGGER = LogManager.getLogger(ChatRoomServer.class);

    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        OptionParser optionParser = new OptionParser();
        OptionSpec<Void> helpSpec = optionParser.accepts("help").forHelp();
        OptionSpec<Integer> portSpec = optionParser.accepts("port", "Server port").withOptionalArg().ofType(Integer.class).defaultsTo(8888);

        try {
            OptionSet optionSet = optionParser.parse(args);

            if (optionSet.has(helpSpec)) {
                optionParser.printHelpOn(System.out);
                return;
            }

            int serverPort = optionSet.valueOf(portSpec);

            try {
                Server server = new Server(serverPort);

                ServerEventListener eventListener = new ServerEventListener(server);
                EventManager.register(eventListener);

                server.start();
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.fatal(String.format("Error starting ChatRoom server: %s", e.getLocalizedMessage()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.fatal(String.format("Error parsing options: %s", e.getLocalizedMessage()));
        }
    }
}
