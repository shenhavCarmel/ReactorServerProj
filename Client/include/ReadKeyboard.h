//
// Created by nofarcar@wincs.cs.bgu.ac.il on 12/31/18.
//
#ifndef BOOST_ECHO_CLIENT_READKEYBOARD_H
#define BOOST_ECHO_CLIENT_READKEYBOARD_H

#include <mutex>
#include "ConnectionHandler.h"
#include <condition_variable>

using namespace std;

class ReadKeyboard {

public:

    ReadKeyboard(ConnectionHandler& conH, condition_variable &cond);
    void run();
    void Register();
    void Login();
    void Logout();
    void FollowUnfollow();
    void Post();
    void PM();
    void UserList();
    void Stat();
    void shortToBytes(short num, char* bytesArr);

private:
    ConnectionHandler& ch;
    mutex lock;
    condition_variable &cond;
    char* ZERO_BYTE;
};

#endif //BOOST_ECHO_CLIENT_READKEYBOARD_H
