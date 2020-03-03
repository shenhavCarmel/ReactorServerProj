package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Client2Server.LoginMessage;
import bgu.spl.net.srv.Commands.Client2Server.LogoutMessage;
import bgu.spl.net.srv.Commands.Message;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKMessage;
import bgu.spl.net.srv.Commands.Server2Client.NotificationMessage;
import bgu.spl.net.srv.Data;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {

    private int connId;
    private Connections connection;
    private boolean shouldTerminate;

    public BidiMessagingProtocolImpl() {
        shouldTerminate = false;
    }

    @Override
    public void start(int connectionId, Connections connections) {
        this.connId = connectionId;
        this.connection = connections;
    }

    @Override
    public void process(Message message) {
        message.setID(connId);
        Message response = message.execute(connection);
        response.setID(connId);
        connection.send(connId, response);

        if (message instanceof LoginMessage && response instanceof ACKMessage){
            while (!Data.pendingMsgs.get(Data.IDUsers.get(connId)).isEmpty()) {
                NotificationMessage m = Data.pendingMsgs.get(Data.IDUsers.get(connId)).poll();
                connection.send(connId, m);
            }
        }
        else if (message instanceof LogoutMessage && response instanceof ACKMessage) {
            shouldTerminate = true;
        }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
