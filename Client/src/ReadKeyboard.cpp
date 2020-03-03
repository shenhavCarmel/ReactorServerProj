//
// Created by nofarcar@wincs.cs.bgu.ac.il on 12/31/18.
//

#include <ReadKeyboard.h>
#include <string>
#include <iostream>


extern bool g_isTerminated;


using namespace std;
using namespace boost;
ReadKeyboard::ReadKeyboard(ConnectionHandler& conH, condition_variable &cond) : ch(conH), cond(cond) {
}

void ReadKeyboard::run() {

    while (!g_isTerminated) {

            string input;
            cin >> input;

            if (input == "REGISTER") {
                Register();
            }

            else if (input == "LOGIN") {
                Login();
            }

            else if (input == "LOGOUT") {
                Logout();
                unique_lock<mutex> uniqueLock(lock);
                cond.wait(uniqueLock);

            }

            else if (input == "FOLLOW") {
                FollowUnfollow();
            }

            else if (input == "POST") {
                Post();
            }

            else if (input == "PM") {
                PM();
            }

            else if (input == "USERLIST") {
                UserList();
            }

            else if (input == "STAT") {
                Stat();
            }
    }

}


void ReadKeyboard::Register() {

    string userName;
    cin >> userName;

    string password;
    cin >> password;

    // send opcode to socket
    char opcode[2];
    short op = 1;
    shortToBytes(op, opcode);
    ch.sendBytes(opcode, 2);

    // send username
    char userNameB[userName.length()+1];
    strcpy(userNameB, userName.c_str());

    ch.sendBytes(userNameB, userName.length());

    // send zero byte
    ch.sendBytes("\0",1);

    // send password
    char passwordB[password.length()+1];
    strcpy(passwordB, password.c_str());
    ch.sendBytes(passwordB, password.length());

    // send zero byte
    ch.sendBytes("\0",1);
}

void ReadKeyboard::Login() {

    string userName;
    cin >> userName;

    string password;
    cin >> password;

    // send opcode to socket
    char opcode[2];
    short op = 2;
    shortToBytes(op, opcode);
    ch.sendBytes(opcode, 2);

    // send username
    char userNameB[userName.length()+1];
    strcpy(userNameB, userName.c_str());

    ch.sendBytes(userNameB, userName.length());

    // send zero byte
    ch.sendBytes("\0",1);

    // send password
    char passwordB[password.length()+1];
    strcpy(passwordB, password.c_str());
    ch.sendBytes(passwordB, password.length());

    // send zero byte
    ch.sendBytes("\0",1);
}

void ReadKeyboard::Logout() {

    // send opcode logout to socket
    char opcode[2];
    short op = 3;
    shortToBytes(op, opcode);
    ch.sendBytes(opcode, 2);
}

void ReadKeyboard::FollowUnfollow() {

    char opcode[2];
    short op = 4;
    shortToBytes(op, opcode);
    ch.sendBytes(opcode, 2);

    // send follow/unfollow
    string followUnfollow;
    cin >> followUnfollow;
    int fu = stoi(followUnfollow);

    if (fu == 0) {
        ch.sendBytes("\0",1);
    }
    else {
        ch.sendBytes("\1",1);
    }

    // send num users
    string numUsers;
    cin >> numUsers;
    short _numUsers = stoi(numUsers);
    char numUsersB[2];
    shortToBytes(_numUsers, numUsersB);
    ch.sendBytes(numUsersB, 2);


    for (int i = 0; i < _numUsers; ++i) {
        string currUserName;
        cin >> currUserName;

        char usersB[currUserName.length()+1];
        strcpy(usersB, currUserName.c_str());
        ch.sendBytes(usersB, currUserName.length());

        // send zero byte
        ch.sendBytes("\0",1);
    }

}

void ReadKeyboard::Post() {

    string content;
    getline(cin, content);
    content = content.substr(1);

    // send opcode
    char opcode[2];
    short op = 5;
    shortToBytes(op, opcode);
    ch.sendBytes(opcode, 2);

    char contentB[content.length()+1];
    strcpy(contentB, content.c_str());
    ch.sendBytes(contentB, content.length());

    // send zero byte
    ch.sendBytes("\0",1);
}

void ReadKeyboard::PM() {

    // send opcode
    char opcode[2];
    short op = 6;
    shortToBytes(op, opcode);
    ch.sendBytes(opcode, 2);

    string userName;
    cin >> userName;

    char userNameB[userName.length()+1];
    strcpy(userNameB, userName.c_str());
    ch.sendBytes(userNameB, userName.length());

    // send zero byte
    ch.sendBytes("\0",1);

    string content;
    getline(cin, content);
    content = content.substr(1);

    char contentB[content.length()+1];
    strcpy(contentB, content.c_str());
    ch.sendBytes(contentB, content.length());

    // send zero byte
    ch.sendBytes("\0",1);
}

void ReadKeyboard::UserList() {

    // send opcode
    char opcode[2];
    short op = 7;
    shortToBytes(op, opcode);
    ch.sendBytes(opcode, 2);
}

void ReadKeyboard::Stat() {

    // send opcode
    char opcode[2];
    short op = 8;
    shortToBytes(op, opcode);
    ch.sendBytes(opcode, 2);

    string userName;
    cin >> userName;

    // send username
    char userNameB[userName.length()+1];
    strcpy(userNameB, userName.c_str());
    ch.sendBytes(userNameB, userName.length());

    // send zero byte
    ch.sendBytes("\0",1);
}

void ReadKeyboard::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}




