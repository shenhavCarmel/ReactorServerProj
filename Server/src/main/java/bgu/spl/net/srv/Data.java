package bgu.spl.net.srv;

import bgu.spl.net.srv.Commands.Message;
import bgu.spl.net.srv.Commands.Server2Client.NotificationMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    // <UserName,Password>
    public static ConcurrentHashMap<String, String> loggedInUsers = new ConcurrentHashMap<>();

    // <Username of client that follows, List of who to follow>
    public static ConcurrentHashMap<String, List<String>> following  = new ConcurrentHashMap<>();

    // <ConnId of client that is being followed, List of followers>
    public static ConcurrentHashMap<String, List<String>> followers  = new ConcurrentHashMap<>();

    // <UserName,Password>
    public static ConcurrentHashMap<String, String> registeredUsers  = new ConcurrentHashMap<>();

    // <ConnID, UserName>
    public static ConcurrentHashMap<Integer, String> IDUsers = new ConcurrentHashMap<>();

    // <UserName, ConnId>
    public static ConcurrentHashMap<String, Integer> UsersID = new ConcurrentHashMap<>();

    // <ConnId, List of the users posts>
    //public static ConcurrentHashMap<Integer, List<String>> posts = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, List<String>> posts = new ConcurrentHashMap<>();

    // <username, List of the users posts>
    public static ConcurrentHashMap<String, List<String>> allPosts = new ConcurrentHashMap<>();

    // <userName, Queue of messages the user didn't receive because he was loggedOf>
    public static ConcurrentHashMap<String, Queue<NotificationMessage>> pendingMsgs = new ConcurrentHashMap<>();
}
