package bgu.spl.net.srv.Commands.Client2Server;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKFollowMessage;
import bgu.spl.net.srv.Commands.Server2Client.ErrorMessage;
import bgu.spl.net.srv.Commands.Message;
import bgu.spl.net.srv.Data;
import org.omg.CORBA.DATA_CONVERSION;

import java.util.LinkedList;
import java.util.List;

public class FollowMessage extends Message {

    private String userName;
    private List<String> usersToFollow;
    private int numOfUsers;

    public FollowMessage(List<String> users, int numOfUsers) {
        this.numOfUsers = numOfUsers;
        this.usersToFollow = users;
    }

    @Override
    public Message execute(Connections connections) {
        this.userName = Data.IDUsers.get(super.userID);
        Message msg;
        List<String> successfull = new LinkedList<>();
        if (Data.IDUsers.containsKey(super.userID) && Data.loggedInUsers.containsKey(userName) && Data.UsersID.get(userName) == super.userID) {
            for (String currUser: usersToFollow)
            {
                if (!Data.following.get(userName).contains(currUser) && Data.registeredUsers.containsKey(currUser) && !currUser.equals(userName)) {
                    Data.following.get(userName).add(currUser);
                    Data.followers.get(currUser).add(userName);
                    successfull.add(currUser);
                }
            }
            if (successfull.size() == 0)
                msg = new ErrorMessage(4);
            else
                msg = new ACKFollowMessage(successfull.size(), successfull, 4);
        }
        else
            msg = new ErrorMessage(4);

        return msg;
    }
}
