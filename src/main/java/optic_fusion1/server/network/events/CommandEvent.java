package optic_fusion1.server.network.events;

import net.lenni0451.asmevents.event.IEvent;
import net.lenni0451.asmevents.event.types.ICancellableEvent;
import optic_fusion1.server.network.ClientConnection;

public class CommandEvent implements IEvent, ICancellableEvent {

    private boolean cancelled = false;
    private final String command;
    private final ClientConnection sender;

    public CommandEvent(ClientConnection sender, String command) {
        this.sender = sender;
        this.command = command;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ClientConnection getSender() {
        return sender;
    }

    public String getCommand() {
        return command;
    }

}
