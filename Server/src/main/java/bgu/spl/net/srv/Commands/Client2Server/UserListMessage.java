package bgu.spl.net.srv.Commands.Client2Server;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Message;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKUserListMessage;
import bgu.spl.net.srv.Commands.Server2Client.ErrorMessage;
import bgu.spl.net.srv.Data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class UserListMessage extends Message {

    private String userName;

    public UserListMessage() {

    }

    @Override
    public Message execute(Connections connections) {

        if (!Data.IDUsers.containsKey(super.userID) || !Data.loggedInUsers.containsKey(Data.IDUsers.get(super.userID)))
            return new ErrorMessage(7);

        this.userName = Data.IDUsers.get(super.userID);

        //Arrays.sort(Data.IDUsers.keySet().toArray());
        List registered = new LinkedList<>();
        Object[] b = Data.IDUsers.keySet().toArray();
        Arrays.sort(b);
        for (int i=0; i<b.length; i++) {
            registered.add(Data.IDUsers.get(b[i]));
        }

        return new ACKUserListMessage(Data.registeredUsers.size(), registered, 7);
    }
}
