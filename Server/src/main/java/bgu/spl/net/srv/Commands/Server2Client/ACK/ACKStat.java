package bgu.spl.net.srv.Commands.Server2Client.ACK;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Message;

public class ACKStat extends ACKMessage {

    private int numPosts;
    private int numFollowers;
    private int numFollowing;

    public ACKStat(int posts, int followers, int following, int opcodeOf) {
        super(opcodeOf);
        numPosts = posts;
        numFollowers = followers;
        numFollowing = following;
    }

    @Override
    public Message execute(Connections connections) {
        return null;
    }

    public  int getNumPosts() {
        return numPosts;
    }

    public int getNumFollowers() {
        return numFollowers;
    }

    public  int getNumFollowing() {
        return numFollowing;
    }

}
