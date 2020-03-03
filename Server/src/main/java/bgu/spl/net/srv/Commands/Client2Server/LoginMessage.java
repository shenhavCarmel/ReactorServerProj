package bgu.spl.net.srv.Commands.Client2Server;


import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKMessage;
import bgu.spl.net.srv.Commands.Server2Client.ErrorMessage;
import bgu.spl.net.srv.Commands.Message;
import bgu.spl.net.srv.Commands.Server2Client.NotificationMessage;
import bgu.spl.net.srv.Data;
import org.omg.CORBA.DATA_CONVERSION;
import org.omg.CORBA.OBJ_ADAPTER;

import java.util.LinkedList;

public class LoginMessage extends Message {

    String userName;
    String password;

    public LoginMessage(String user, String pass) {
        userName = user;
        password = pass;
    }

    @Override
    public Message execute(Connections connections) {

        // check if the user is registered and not already logged in
        if (Data.registeredUsers.containsKey(userName) && !Data.loggedInUsers.containsKey(userName) && Data.registeredUsers.get(userName).equals(password)) {

            Object LOCK = new Object();

            synchronized (LOCK) {
                // init data structures
                String result = Data.loggedInUsers.putIfAbsent(userName, password);

                if (result != null)
                    return new ErrorMessage(1);

                // update connection ID to be the current one in all data structures
                int oldConnId = Data.UsersID.get(userName);
                Data.UsersID.replace(userName, oldConnId, super.userID);
                Data.IDUsers.remove(oldConnId);
                Data.IDUsers.put(super.userID, userName);
            }

            return new ACKMessage(2);
        }
        return new ErrorMessage(2);
    }
}
