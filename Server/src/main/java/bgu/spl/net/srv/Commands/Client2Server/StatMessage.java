package bgu.spl.net.srv.Commands.Client2Server;


import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Message;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKStat;
import bgu.spl.net.srv.Commands.Server2Client.ErrorMessage;
import bgu.spl.net.srv.Data;

import java.io.Serializable;

public class StatMessage extends Message {

    private String userToStat;
    private String userName;

    public StatMessage(String name) {
        userToStat = name;
    }

    @Override
    public Message execute(Connections connections) {


        userName = Data.IDUsers.get(super.userID);

        if (!Data.IDUsers.containsKey(super.userID) || !Data.loggedInUsers.containsKey(userName) || !Data.registeredUsers.containsKey(userToStat))
            return new ErrorMessage(8);

        return new ACKStat(Data.posts.get(userToStat).size(), Data.followers.get(userName).size(),
                            Data.following.get(userName).size(), 8);
    }
}
