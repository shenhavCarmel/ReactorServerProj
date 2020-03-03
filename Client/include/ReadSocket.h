//
// Created by nofarcar@wincs.cs.bgu.ac.il on 12/31/18.
//

#ifndef BOOST_ECHO_CLIENT_READSOCKET_H
#define BOOST_ECHO_CLIENT_READSOCKET_H

#include "ConnectionHandler.h"
#include <condition_variable>
using namespace std;


class ReadSocket {

public:
    ReadSocket(ConnectionHandler& connH, condition_variable &cond);
    void run();
    string Notification();
   // string ACK();
    string ACKFollowMessage();
    string ACKStat();
    string ACKUserListMessage();
    string Error();
    char* substr(char* arr, int begin, int len);

private:
    ConnectionHandler& ch;
    condition_variable &cond;
};


#endif //BOOST_ECHO_CLIENT_READSOCKET_H
