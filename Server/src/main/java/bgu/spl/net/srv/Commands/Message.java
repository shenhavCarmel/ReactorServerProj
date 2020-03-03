package bgu.spl.net.srv.Commands;

import bgu.spl.net.api.bidi.Connections;

import java.io.Serializable;

public abstract class Message implements Serializable {

    protected Integer userID;

    public abstract Message execute(Connections connections);

    public void setID(int newUserID) {
        userID = newUserID;
    }

}
