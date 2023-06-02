package com.example.vchatserver.gateway;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static Map<Long, SocketIOClient> sessions = new ConcurrentHashMap<>();

    public static void addSession(Long userId, SocketIOClient client) {
        sessions.put(userId, client);
    }

    public static void removeSession(Long userId) {
        sessions.remove(userId);
    }

    public static SocketIOClient getSession(Long userId) {
        return sessions.get(userId);
    }
}