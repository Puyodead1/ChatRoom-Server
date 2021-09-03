/*
 * Copyright (C) 2021 Optic_Fusion1
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package optic_fusion1.server.commands;

import optic_fusion1.commands.command.Command;
import optic_fusion1.commands.command.CommandSender;
import optic_fusion1.common.data.Message;
import optic_fusion1.packets.OpCode;
import optic_fusion1.packets.impl.MessagePacket;
import optic_fusion1.server.network.ClientConnection;
import optic_fusion1.server.network.SocketServer;

import java.util.List;
import java.util.Objects;

public class RegisterCommand extends Command {

  private final SocketServer server;

  public RegisterCommand(SocketServer server) {
    super("register");
    this.server = server;
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, List<String> args) {
    ClientConnection client = (ClientConnection) sender;
    if (args.size() != 3) {
      client.sendPacket(new MessagePacket(OpCode.MESSAGE, new Message(null, "Usage: /register <username> <password> <password again>").serialize(), MessagePacket.MessageChatType.SYSTEM));
      return true;
    }
    if (!Objects.equals(args.get(1), args.get(2))) {
      client.sendPacket(new MessagePacket(OpCode.MESSAGE, new Message(null, "Passwords do not match").serialize(), MessagePacket.MessageChatType.SYSTEM));
      return true;
    }
    server.createAccount(client, args.get(0), args.get(1));
    return true;
  }

}
