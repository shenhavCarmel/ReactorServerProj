#include <stdlib.h>
#include <thread>
#include "ReadKeyboard.h"
#include "ReadSocket.h"
#include "ConnectionHandler.h"

bool g_isTerminated = false;


/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
using namespace boost;


int main (int argc, char *argv[]) {

    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }

    std::string host = argv[1];
    short port = atoi(argv[2]);
/*
    std::string host = "127.0.0.1";
    short port = 7777;
*/
    ConnectionHandler connectionHandler(host, port);

    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    condition_variable cond;
    ReadSocket socketListener(connectionHandler, cond);
    std::thread t1(&ReadSocket::run, &socketListener);
    ReadKeyboard keyboardListener(connectionHandler, cond);
    std::thread t2(&ReadKeyboard::run, &keyboardListener);
    t1.join();
    t2.join();

    return 0;

}
