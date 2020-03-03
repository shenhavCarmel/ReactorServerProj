package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.srv.Commands.*;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKFollowMessage;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKMessage;
import bgu.spl.net.srv.Commands.Client2Server.*;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKStat;
import bgu.spl.net.srv.Commands.Server2Client.ACK.ACKUserListMessage;
import bgu.spl.net.srv.Commands.Server2Client.ErrorMessage;
import bgu.spl.net.srv.Commands.Server2Client.NotificationMessage;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private byte[] messageBytes;
    private int messageBytesIndex;
    private short currOpcode;
    private int currStage;
    private int opcodeCounter;
    private boolean finishMsg;
    private int counter;

    public MessageEncoderDecoderImpl() {
        messageBytes = new byte[1 << 10];
        messageBytesIndex = 0;
        currStage = 0;
        opcodeCounter = 0;
        counter = 0;
        finishMsg = false;
    }

    @Override
    public Message decodeNextByte(byte nextByte) {
        Message m = null;
        if (opcodeCounter == 0) {
            messageBytes[messageBytesIndex] = nextByte;
            messageBytesIndex++;
            opcodeCounter++;
        }
        else if (opcodeCounter == 1) {
            messageBytes[messageBytesIndex] = nextByte;
            currOpcode = bytesToShort(messageBytes);
            currStage++;
            messageBytesIndex++;
            if (currOpcode == 3) {

                // LOGOUT
                m = processToLogout();
                opcodeCounter = 0;
                messageBytesIndex = 0;
                currStage = 0;
                messageBytes = new byte[1 << 10];
                currOpcode = 0;
                finishMsg = false;
                return m;
            }
            else if (currOpcode == 7) {

                // userlist
                m = processToUserList();
                opcodeCounter = 0;
                messageBytesIndex = 0;
                currStage = 0;
                messageBytes = new byte[1 << 10];
                currOpcode = 0;
                finishMsg = false;
                return m;
            }
            else
                opcodeCounter++;
        }
        else if (opcodeCounter == 2) {
            switch (currOpcode) {
                // register
                case 1:
                    messageBytes[messageBytesIndex] = nextByte;
                    messageBytesIndex++;
                    if (nextByte == 0)
                        currStage++;
                    if (currStage == 3) {
                        finishMsg = true;
                        m = processToRegister();
                    }
                    break;

                // login
                case 2:
                    messageBytes[messageBytesIndex] = nextByte;
                    messageBytesIndex++;
                    if (nextByte == 0)
                        currStage++;
                    if (currStage == 3) {
                        finishMsg = true;
                        m = processToLogin();
                    }
                    break;
                // follow
                case 4:
                    messageBytes[messageBytesIndex] = nextByte;
                    messageBytesIndex++;
                    if (messageBytesIndex == 5) {
                        byte[] numUsers = new byte[2];
                        numUsers[0] = messageBytes[3];
                        numUsers[1] = messageBytes[4];
                        counter = bytesToShort(numUsers);
                    }
                    if (nextByte == 0) {
                        counter--;
                        if (counter == 0) {
                            finishMsg = true;
                            m = processToFollow();
                        }
                    }
                    break;
                // post
                case 5:
                    messageBytes[messageBytesIndex] = nextByte;
                    messageBytesIndex++;
                    if (nextByte == 0) {
                        finishMsg = true;
                        m = processToPost();
                    }
                    break;
                // pm
                case 6:
                    messageBytes[messageBytesIndex] = nextByte;
                    messageBytesIndex++;
                    if (nextByte == 0)
                        currStage++;
                    if (currStage == 3) {
                        finishMsg = true;
                        m = processToPM();
                    }
                    break;
                // stat
                case 8:
                    messageBytes[messageBytesIndex] = nextByte;
                    messageBytesIndex++;
                    if (nextByte == 0){
                        finishMsg = true;
                        m = processToStat();
                    }
                    break;
            }
            if (finishMsg) {
                currStage = 0;
                messageBytesIndex = 0;
                messageBytes = new byte[1 << 10];
                opcodeCounter = 0;
                currOpcode = 0;
                finishMsg = false;
                return m;
            }
        }

        return null;
    }

    @Override
    public byte[] encode(Message message) {
        if (message instanceof NotificationMessage) {
            return encodeNotification((NotificationMessage)message);
        }
        else if (message instanceof ErrorMessage) {
            return encodeError((ErrorMessage)message);
        }
        else if (message instanceof ACKFollowMessage) {
            return encodeAckFollow((ACKFollowMessage)message);
        }
        else if (message instanceof ACKStat) {
            return encodeAckStat((ACKStat)message);
        }
        else if (message instanceof ACKUserListMessage) {
            return encodeAckUserList((ACKUserListMessage)message);
        }
        else if (message instanceof ACKMessage) {
            return encodeACK((ACKMessage)message);
        }
        else {
            throw new IllegalArgumentException("Can't encode message, incorrect instance");
        }

    }

    private Message processToRegister() {
        int advanceAmount;
        int startIndex = 2;
        advanceAmount = calcNeededBytes(startIndex);
        String userName = new String(messageBytes, startIndex, advanceAmount, StandardCharsets.UTF_8);

        startIndex = startIndex + advanceAmount+1;
        advanceAmount = calcNeededBytes(startIndex);
        String password = new String(messageBytes, startIndex, advanceAmount, StandardCharsets.UTF_8);

        Message m = new RegisterMessage(userName, password);
        return  m;
    }

    private Message processToLogin() {
        int advanceAmount;
        int startIndex = 2;
        advanceAmount = calcNeededBytes(startIndex);
        String userName = new String(messageBytes, startIndex, advanceAmount, StandardCharsets.UTF_8);

        startIndex = startIndex + advanceAmount+1;
        advanceAmount = calcNeededBytes(startIndex);
        String password = new String(messageBytes, startIndex, advanceAmount, StandardCharsets.UTF_8);


        Message m = new LoginMessage(userName, password);
        return  m;
    }

    private Message processToLogout() {

        return new LogoutMessage();
    }

    private Message processToFollow() {

        Message msg;

        boolean follow = (messageBytes[2] == 0);

        // get num of users to un/follow
        byte[] numToFollow = new byte[2];
        numToFollow[0] = messageBytes[3];
        numToFollow[1] = messageBytes[4];
        int numFollow = bytesToShort(numToFollow);

        List<String> usersList = new LinkedList<>();
        int startIndex = 5;
        int advanceAmount;

        int userCounter = numFollow;
        while (userCounter > 0) {

            advanceAmount = calcNeededBytes(startIndex);
            usersList.add(new String(messageBytes, startIndex, advanceAmount, StandardCharsets.UTF_8));
            userCounter--;
            startIndex = startIndex + advanceAmount + 1;
        }

        if (follow)
            msg = new FollowMessage(usersList, numFollow);
        else
            msg = new UnfollowMessage(usersList, numFollow);

        return msg;
    }

    private Message processToPost() {

        String content = new String(messageBytes, 2,  calcNeededBytes(2), StandardCharsets.UTF_8);
        return new PostMessage(content);
    }

    private Message processToPM() {

        int startIndex = 2;
        int advanceAmount = calcNeededBytes(startIndex);
        String receipient = new String(messageBytes, startIndex, advanceAmount, StandardCharsets.UTF_8);

        startIndex = startIndex + advanceAmount + 1;
        advanceAmount = calcNeededBytes(startIndex);
        String content = new String(messageBytes, startIndex, advanceAmount, StandardCharsets.UTF_8);

        return new PMMessage(receipient, content);
    }

    private Message processToUserList() {

        return new UserListMessage();
    }

    private Message processToStat() {

        int startIndex = 2;
        int advanceAmount = calcNeededBytes(startIndex);
        String userName = new String(messageBytes, startIndex, advanceAmount, StandardCharsets.UTF_8);

        return new StatMessage(userName);
    }

    private byte[] encodeNotification(NotificationMessage msg) {

        byte[] opcode = shortToBytes((short) 9);
        byte notificationType = (byte)msg.getType();
        byte[] postingUser = msg.getPostingUser().getBytes();
        byte[] content = msg.getContent().getBytes();

        int size = opcode.length + 1 + postingUser.length + 1 + content.length + 1;
        byte[] encoded = new byte[size];
        encoded[0] = opcode[0];
        encoded[1] = opcode[1];
        encoded[2] = notificationType;
        int arrayIndex = 3;
        insertArrays(encoded, postingUser, arrayIndex);
        arrayIndex = arrayIndex + postingUser.length;
        encoded[arrayIndex] = 0;
        arrayIndex++;
        insertArrays(encoded, content, arrayIndex);
        arrayIndex = arrayIndex + content.length;
        encoded[arrayIndex] = 0;

        return encoded;

    }

    private byte[] encodeACK(ACKMessage msg) {

        byte[] opcodeAck = shortToBytes((short) 10);
        byte[] opcodeOf = shortToBytes((short) msg.getOpcodeOf());
        byte[] encode = new byte[4];
        encode[0] = opcodeAck[0];
        encode[1] = opcodeAck[1];
        encode[2] = opcodeOf[0];
        encode[3] = opcodeOf[1];
        return encode;
    }

    private byte[] encodeError(ErrorMessage msg) {

        byte[] opcodeError = shortToBytes((short) 11);
        byte[] opcodeOf = shortToBytes((short) msg.getOpcodeOf());
        byte[] encode = new byte[4];
        encode[2] = opcodeOf[0];
        encode[3] = opcodeOf[1];
        encode[0] = opcodeError[0];
        encode[1] = opcodeError[1];

        return encode;

    }

    private byte[] encodeAckUserList(ACKUserListMessage message) {

        byte[] opcodeAck = shortToBytes((short) 10);
        byte[] opcodeUserList = shortToBytes((short)7);
        byte[] numOfUsers = shortToBytes((short)message.getNumOfUsers());
        byte[][] userList = new byte[message.getNumOfUsers()][];
        int size = 6;
        for (int i=0; i<userList.length; i++) {
            userList[i] = message.getUsersNameList().get(i).getBytes();
            size = size + userList[i].length + 1;
        }

        byte[] encode = new byte[size];
        encode[0] = opcodeAck[0];
        encode[1] = opcodeAck[1];
        encode[2] = opcodeUserList[0];
        encode[3] = opcodeUserList[1];
        encode[4] = numOfUsers[0];
        encode[5] = numOfUsers[1];
        int arrayIndex = 6;
        for (int i=0; i<userList.length; i++) {
            insertArrays(encode,userList[i],arrayIndex);
            arrayIndex = arrayIndex + userList[i].length;
            encode[arrayIndex] = 0;
            arrayIndex++;
        }

        return encode;

    }

    private byte[] encodeAckStat(ACKStat message) {

        byte[] opcodeAck = shortToBytes((short) 10);
        byte[] opcodeOf = shortToBytes((short) 8);
        byte[] numPosts = shortToBytes((short)message.getNumPosts());
        byte[] numFollowers = shortToBytes((short)message.getNumFollowers());
        byte[] numFollowing = shortToBytes((short)message.getNumFollowing());
        byte[] encode = new byte[10];
        encode[0] = opcodeAck[0];
        encode[1] = opcodeAck[1];
        encode[2] = opcodeOf[0];
        encode[3] = opcodeOf[1];
        encode[4] = numPosts[0];
        encode[5] = numPosts[1];
        encode[6] = numFollowers[0];
        encode[7] = numFollowers[1];
        encode[8] = numFollowing[0];
        encode[9] = numFollowing[1];

        return encode;

    }

    private byte[] encodeAckFollow(ACKFollowMessage message) {

        byte[] opcodeAck = shortToBytes((short) 10);
        byte[] opcodeOf = shortToBytes((short) 4);
        byte[] numUsers = shortToBytes((short)message.getNumOfSuccessfullUsers());

        byte[][] userLst = new byte[message.getNumOfSuccessfullUsers()][];
        int size = 6;
        for (int i=0; i<userLst.length; i++) {
            userLst[i] = message.getUsersList().get(i).getBytes();
            size = size + userLst[i].length + 1;
        }

        byte[] encode = new byte[size];
        encode[0] = opcodeAck[0];
        encode[1] = opcodeAck[1];
        encode[2] = opcodeOf[0];
        encode[3] = opcodeOf[1];
        encode[4] = numUsers[0];
        encode[5] = numUsers[1];
        int arrayIndex = 6;
        for (int i=0; i<userLst.length; i++) {
            insertArrays(encode,userLst[i],arrayIndex);
            arrayIndex = arrayIndex + userLst[i].length;
            encode[arrayIndex] = 0;
            arrayIndex++;
        }
        return encode;

    }

    private int calcNeededBytes(int indexStartByte) {

        int i = indexStartByte;
        Byte nextB = messageBytes[i];
        int byteCounter = 0;

        while (nextB != 0) {
            byteCounter++;
            i++;
            nextB = messageBytes[i];
        }
        return byteCounter;
    }

    private void insertArrays(byte[] insertInto, byte[] insertFrom, int startIndex) {
        for (int i=0; i<insertFrom.length; i++) {
            insertInto[startIndex] = insertFrom[i];
            startIndex++;
        }
    }

    private short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    private byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}
