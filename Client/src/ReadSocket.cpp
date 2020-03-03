//
// Created by nofarcar@wincs.cs.bgu.ac.il on 12/31/18.
//
#include "ReadSocket.h"


extern bool g_isTerminated;

using namespace std;
using namespace boost;

ReadSocket::ReadSocket(ConnectionHandler& connH, condition_variable &cond): ch(connH), cond(cond) {

}

void ReadSocket::run() {

    while (!g_isTerminated) {
        std::string response;

        char opcodeChar[2];
        bool succeeded = ch.getBytes(opcodeChar, 2);
        if (succeeded) {
            short opcode = ch.bytesToShort(opcodeChar);

            switch (opcode) {
                case 9:
                    response = Notification();
                    break;
                case 10:
                {
                    char msgOpcodeChar[2];
                    succeeded = ch.getBytes(msgOpcodeChar,2);
                    if (succeeded) {
                        short ackOpcode = ch.bytesToShort(msgOpcodeChar);
                        switch (ackOpcode) {
                            case 1:
                                response = "ACK 1";
                                break;
                            case 2:
                                response = "ACK 2";
                                break;
                            case 3:
                                response = "ACK 3";
                                g_isTerminated = true;
                                cond.notify_all();
                                break;
                            case 4:
                                response = ACKFollowMessage();
                                break;
                            case 5:
                                response = "ACK 5";
                                break;
                            case 6:
                                response = "ACK 6";
                                break;
                            case 7:
                                response = ACKUserListMessage();
                                break;
                            case 8:
                                response = ACKStat();
                                break;
                            default:
                                break;
                        }
                    }
                }
                    break;
                case 11:
                    response = Error();
                    break;
                default:
                    break;
            }
        }
        if (!response.empty()) {
            cout << response << std::endl;
        }
        else
            cout << "something went wrong with getBytes" << endl;
    }
}


string ReadSocket::ACKFollowMessage() {
    bool succeeded;
    char numUsersChar[2];
    succeeded = ch.getBytes(numUsersChar, 2);
    if (succeeded) {
        short numUsers = ch.bytesToShort(numUsersChar);
        string users;
        for (int i=0; i<numUsers; i++) {
            string user;
            ch.getFrameAscii(user,'\0');
            user = user.substr(0, user.size() -1);
            users += " " + user;
        }

        users = users.substr(1);
        string result = "ACK 4 " + to_string(numUsers) + " " + users;

        return result;
    }

    return "";
}

string ReadSocket::ACKStat() {
    bool succeeded;
    char msg[6];
    succeeded = ch.getBytes(msg, 6);

    if (succeeded) {

        char * numPostsChar = substr(msg, 0, 2);
        short numPosts = ch.bytesToShort(numPostsChar);
        delete numPostsChar;

        char * numPostsFollowersChar = substr(msg, 2, 2);
        short numFollowers = ch.bytesToShort(numPostsFollowersChar);
        delete numPostsFollowersChar;

        char * numPostsFollowingChar = substr(msg, 4, 2);
        short numFollowing = ch.bytesToShort(numPostsFollowingChar);
        delete numPostsFollowingChar;

        return "ACK 8 " + to_string(numPosts) + " " + to_string(numFollowers) + " " + to_string(numFollowing);
    }

    return "";

}

string ReadSocket::ACKUserListMessage() {

    bool succeeded;
    char numUsersChar[2];
    succeeded = ch.getBytes(numUsersChar, 2);
    if (succeeded) {
        short numUsers = ch.bytesToShort(numUsersChar);
        string users;
        for (int i=0; i<numUsers; i++) {
            string user;
            ch.getFrameAscii(user,'\0');
            user = user.substr(0,user.length()-1);
            users += " " + user;
        }
        users = users.substr(1);
        string result = "ACK 7 " + to_string(numUsers) +" "+ users;
        return result;
    }

    return "";
}

string ReadSocket::Error() {
    bool  succeeded;
    char msgOpcodeChar[2];
    succeeded = ch.getBytes(msgOpcodeChar, 2);
    if (succeeded) {
        short msgOpcode = ch.bytesToShort(msgOpcodeChar);
        if(msgOpcode)
            cond.notify_all();
        return "ERROR " + to_string(msgOpcode);
    }

    return "";

}

string ReadSocket::Notification() {
    bool succeeded;
    char notiType[1];
    succeeded = ch.getBytes(notiType, 1);
    if (succeeded) {

        // get type msg
        bool isPmMsg = notiType[0] == 0;
        string type;
        if (isPmMsg)
            type = "PM";
        else
            type = "Public";

        string postingUser;
        ch.getFrameAscii(postingUser,'\0');
        postingUser = postingUser.substr(0, postingUser.length()-1);
        string content;
        ch.getFrameAscii(content,'\0');
        content = content.substr(0, content.length()-1);

        return "NOTIFICATION " + type + " " + postingUser + " " + content;
    }

    return "";
}

char* ReadSocket::substr(char *arr, int begin, int len) {
    char* res = new char[len];
    for (int i = 0; i < len; i++)
        res[i] = *(arr + begin + i);
    res[len] = 0;
    return res;
}
