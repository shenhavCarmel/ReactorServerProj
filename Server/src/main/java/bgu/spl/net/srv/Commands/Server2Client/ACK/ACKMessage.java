package bgu.spl.net.srv.Commands.Server2Client.ACK;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Message;

import java.io.Serializable;

public class ACKMessage extends Message {

    private int opcodeOf;

    public ACKMessage(int opcodeOf) {
        this.opcodeOf = opcodeOf;
    }

    @Override
    public Message execute(Connections connections) {
        return null;
    }

    public int getOpcodeOf() {
        return opcodeOf;
    }
}
