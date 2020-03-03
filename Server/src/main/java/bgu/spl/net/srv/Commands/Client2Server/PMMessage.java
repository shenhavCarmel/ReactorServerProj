package bgu.spl.net.srv.Commands.Client2Server;


import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Message;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKMessage;
import bgu.spl.net.srv.Commands.Server2Client.ErrorMessage;
import bgu.spl.net.srv.Commands.Server2Client.NotificationMessage;
import bgu.spl.net.srv.Data;

import java.io.Serializable;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class PMMessage extends Message {

    private String messageContent;
    private String receipient;
    private String sender;

    public PMMessage(String receipient, String message) {
        this.receipient = receipient;
        this.messageContent = message;
    }

    @Override
    public Message execute(Connections connections) {
        this.sender = Data.IDUsers.get(super.userID);
        if (!Data.IDUsers.containsKey(super.userID) || !Data.registeredUsers.containsKey(sender) || !Data.loggedInUsers.containsKey(sender) || !Data.registeredUsers.containsKey(receipient))
            return new ErrorMessage(6);

        Data.allPosts.get(sender).add(messageContent);

        short sh = 0;
        NotificationMessage noti = new NotificationMessage(sh, sender, messageContent);

        // the user is logged in
        if (Data.loggedInUsers.containsKey(receipient)) {
            connections.send(Data.UsersID.get(receipient), noti);
        }
        else {
            if (Data.pendingMsgs.get(receipient) == null)
                Data.pendingMsgs.put(receipient, new LinkedBlockingQueue<>());
            Data.pendingMsgs.get(receipient).add(noti);
        }


        return new ACKMessage(6);
    }
}
