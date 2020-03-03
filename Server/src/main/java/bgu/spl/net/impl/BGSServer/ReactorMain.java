package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.srv.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;


public class ReactorMain {

    public static void main(String[] args) {
        Server.reactor(
                Integer.parseInt(args[1]), //num threads
                Integer.parseInt(args[0]), //port
                () ->  new BidiMessagingProtocolImpl(), //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();

    }
}
