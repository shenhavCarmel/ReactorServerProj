package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.ConnectionHandler;
import bgu.spl.net.api.bidi.Connections;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T>{

    private ConcurrentHashMap<Integer, ConnectionHandler> activeClientsHandler;
    private ConcurrentHashMap<ConnectionHandler, Integer> handlerActiveClients;

    public ConnectionsImpl() {
        activeClientsHandler = new ConcurrentHashMap<>();
        handlerActiveClients = new ConcurrentHashMap<>();
    }

    @Override
    public boolean send(int connectionId, T msg) {
        if (!activeClientsHandler.containsKey(connectionId))
            return false;
        activeClientsHandler.get(connectionId).send(msg);
        return true;
    }

    @Override
    public void broadcast(T msg) {
        Iterator it = activeClientsHandler.entrySet().iterator();
        while (it.hasNext()) {
            activeClientsHandler.get(it.next()).send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        if (activeClientsHandler.containsKey(connectionId)) {
            activeClientsHandler.remove(connectionId);
        }
    }

    public void addConnectionHandler(ConnectionHandler ch, Integer id) {
        if (!activeClientsHandler.containsKey(id) && !handlerActiveClients.containsKey(ch)) {
            activeClientsHandler.put(id,ch);
            handlerActiveClients.put(ch,id);
        }
    }
}
