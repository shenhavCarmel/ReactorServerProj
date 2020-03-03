package bgu.spl.net.srv.Commands.Client2Server;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKMessage;
import bgu.spl.net.srv.Commands.Server2Client.ErrorMessage;
import bgu.spl.net.srv.Commands.Message;
import bgu.spl.net.srv.Data;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class RegisterMessage extends Message {

    private String userName;
    private String password;

    public RegisterMessage(String user, String pass) {
        userName = user;
        password = pass;
    }

    @Override
    public Message execute(Connections connections) {
        if(Data.registeredUsers.containsKey(userName))
            return new ErrorMessage(1);

        else {

            Object lock = new Object();

            synchronized (lock) {
                // init data structures
                String result = Data.registeredUsers.putIfAbsent(userName, password);
                if (result != null)
                    return new ErrorMessage(1);


                Data.pendingMsgs.put(userName, new LinkedBlockingQueue<>());
                Data.following.put(userName, new LinkedList<>());
                Data.followers.put(userName, new LinkedList<>());
                Data.IDUsers.put(super.userID, userName);
                Data.UsersID.put(userName, super.userID);
                Data.posts.put(userName, new LinkedList<>());
                Data.allPosts.put(userName, new LinkedList<>());
            }

            return new ACKMessage(1);
        }
    }
}
