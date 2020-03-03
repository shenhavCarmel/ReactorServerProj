package bgu.spl.net.srv.Commands.Server2Client.ACK;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Message;

import java.util.List;

public class ACKFollowMessage extends ACKMessage {

    private int numOfSuccessfullUsers;
    private List<String> usersList;

    public ACKFollowMessage(int numOfUsers, List<String> usersList, int opcodeOf) {
        super(opcodeOf);
        this.numOfSuccessfullUsers = numOfUsers;
        this.usersList = usersList;

    }

    public  int getNumOfSuccessfullUsers() {
        return numOfSuccessfullUsers;
    }

    public  List<String> getUsersList() {
        return usersList;
    }

    @Override
    public Message execute(Connections connections) {
        return null;
    }
}
