package bgu.spl.net.srv.Commands.Client2Server;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKMessage;
import bgu.spl.net.srv.Commands.Server2Client.ErrorMessage;
import bgu.spl.net.srv.Commands.Message;
import bgu.spl.net.srv.Data;

public class LogoutMessage extends Message {

    String userName;

    public LogoutMessage() {

    }

    @Override
    public Message execute(Connections connections) {
        if (Data.IDUsers.containsKey(super.userID)) {
            userName = Data.IDUsers.get(super.userID);
            if (Data.registeredUsers.containsKey(userName) && Data.loggedInUsers.containsKey(userName)) {
                Data.loggedInUsers.remove(userName);
                return new ACKMessage(3);
            }
        }

        return new ErrorMessage(3);
    }
}
