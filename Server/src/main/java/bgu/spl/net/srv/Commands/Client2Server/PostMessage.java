package bgu.spl.net.srv.Commands.Client2Server;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.Commands.Message;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKMessage;
import bgu.spl.net.srv.Commands.Server2Client.ErrorMessage;
import bgu.spl.net.srv.Commands.Server2Client.NotificationMessage;
import bgu.spl.net.srv.Data;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class PostMessage extends Message {

    private final char TAG = '@';
    private String content;
    private List<String> taggedUsers;
    private List<String> receipients;
    private String sender;

    public PostMessage(String content) {
        this.content = content;
        taggedUsers = new LinkedList<>();
        receipients = new LinkedList<>();
        initTaggedUsers();

    }

    private void initReceipients() {

        for (String taggedUser: taggedUsers)
        {
            receipients.add(taggedUser);
        }

        for (String userReceipient: Data.followers.get(sender))  {
            if (!receipients.contains(userReceipient))
                receipients.add(userReceipient);
        }
    }

    private void initTaggedUsers() {

        // collect tagged users
        int i = 0;
        while (i < content.length()) {
            if (content.charAt(i) == TAG) {
                i++;
                String taggedUser = "";

                while (i<content.length() && content.charAt(i) != ' ') {
                    taggedUser += content.charAt(i);
                    i++;
                }

                if (Data.registeredUsers.containsKey(taggedUser) && !taggedUsers.contains(taggedUser))
                    taggedUsers.add(taggedUser);
            }
            i++;
        }
    }

    @Override
    public Message execute(Connections connections) {
        this.sender = Data.IDUsers.get(super.userID);


        if (!Data.IDUsers.containsKey(super.userID) || Data.UsersID.get(sender) != super.userID || !Data.loggedInUsers.containsKey(sender))
            return new ErrorMessage(5);

        initReceipients();

        Data.posts.get(sender).add(content);
        Data.allPosts.get(sender).add(content);

        for (String receipient: receipients) {
            short s = 1;
            NotificationMessage noti = new NotificationMessage(s, sender, content);

            // the receipient is logged in
            if (Data.loggedInUsers.containsKey(receipient)) {
                connections.send(Data.UsersID.get(receipient), noti);
            }

            // the receipient isn't logged in, add to pending msgs
            else {
                if (Data.pendingMsgs.get(receipient) == null)
                    Data.pendingMsgs.put(receipient, new LinkedBlockingQueue<>());
                Data.pendingMsgs.get(receipient).add(noti);
            }
        }

        return new ACKMessage(5);
    }
}
