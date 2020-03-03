package bgu.spl.net.srv.Commands.Server2Client;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Client2Server.PMMessage;
import bgu.spl.net.srv.Commands.Client2Server.PostMessage;
import bgu.spl.net.srv.Commands.Message;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKMessage;
import bgu.spl.net.srv.Data;

import java.io.Serializable;

public class NotificationMessage extends Message {

    private short type;
    private String sender;
    private String content;
    private boolean isPmMsg;

    public NotificationMessage(short type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.isPmMsg = type == 0;
    }

    @Override
    public Message execute(Connections connections) {
        return null;
/*
        // Resend the message of the notification
        if (isPmMsg) {

        }
        else {
            PostMessage m = new PostMessage(content);
            m.execute(connections);
        }
        Data.pendingMsgs.get(super.userID).remove(this);
        return new ACKMessage(9);
        */
    }

    public short getType() {
        return type;
    }

    public String getPostingUser() {
        return sender;
    }

    public String getContent() {
        return content;
    }

}
