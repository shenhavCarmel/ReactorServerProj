package bgu.spl.net.srv.Commands.Server2Client;


import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Message;

public class ErrorMessage extends Message {

    private int opcodeOf;

    public ErrorMessage(int opcodeOf) {
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
