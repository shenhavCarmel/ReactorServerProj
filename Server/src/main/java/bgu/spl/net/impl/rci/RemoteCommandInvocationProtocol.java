/*
package bgu.spl.net.impl.rci;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.srv.Commands.Message;

import java.io.Serializable;

public class RemoteCommandInvocationProtocol<T> implements BidiMessagingProtocol<Serializable> {

    private T arg;

    public RemoteCommandInvocationProtocol(T arg) {
        this.arg = arg;
    }

    @Override
    public Serializable process(Serializable msg) {
        return ((Message) msg).execute(arg);
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }

}
*/