package dev.protoskey.point;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/updates")
public class UpdatesEndpoint {
    private static final Set<Session> SESSIONS = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        SESSIONS.remove(session);
    }

    public static void broadcast(String topic) {
        String payload = "{\"topic\":\"" + topic + "\"}";
        for (Session session : SESSIONS) {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(payload);
            } else {
                SESSIONS.remove(session);
            }
        }
    }
}
