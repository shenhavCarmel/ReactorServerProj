CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lpthread

all: EchoClient
	g++ -o bin/BGSclient bin/ConnectionHandler.o bin/echoClient.o bin/ReadSocket.o bin/ReadKeyboard.o $(LDFLAGS) 

EchoClient: bin/ConnectionHandler.o bin/echoClient.o bin/ReadSocket.o bin/ReadKeyboard.o
	
bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ $(CFLAGS) -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp	
	
bin/ReadSocket.o: src/ReadSocket.cpp
	g++ $(CFLAGS) -o bin/ReadSocket.o src/ReadSocket.cpp
	
bin/ReadKeyboard.o: src/ReadKeyboard.cpp
	g++ $(CFLAGS) -o bin/ReadKeyboard.o src/ReadKeyboard.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
