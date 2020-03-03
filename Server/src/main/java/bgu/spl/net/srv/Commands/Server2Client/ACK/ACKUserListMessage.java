package bgu.spl.net.srv.Commands.Server2Client.ACK;


import bgu.spl.net.srv.Commands.Message;

import java.util.List;

public class ACKUserListMessage extends ACKMessage {

    private int numOfUsers;
    private List<String> usersNameList;

    public ACKUserListMessage(int numOfUsers, List<String> usersList, int opcodeOf) {
        super(opcodeOf);
        this.numOfUsers = numOfUsers;
        this.usersNameList = usersList;
    }

    public int getNumOfUsers() {
        return numOfUsers;
    }

    public List<String> getUsersNameList() {
        return usersNameList;
    }
}
