package bgu.spl.net.impl.BGSServer;
import bgu.spl.net.srv.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;

public class TPCMain {

    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);

        Server.threadPerClient(port, () -> new BidiMessagingProtocolImpl(), () ->new MessageEncoderDecoderImpl()).serve();

    }
}
