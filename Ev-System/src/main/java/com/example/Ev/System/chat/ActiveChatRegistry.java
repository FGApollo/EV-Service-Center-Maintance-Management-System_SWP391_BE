package com.example.Ev.System.chat;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActiveChatRegistry {

    public static class ActiveChat {
        public final String sessionId;
        public final long createdAt;
        public volatile long lastActivity;

        public ActiveChat(String sessionId) {
            this.sessionId = sessionId;
            this.createdAt = Instant.now().toEpochMilli();
            this.lastActivity = this.createdAt;
        }
    }

    private final Map<String, ActiveChat> sessions = new ConcurrentHashMap<>();

    // Trả về true nếu là session mới
    public boolean registerIfAbsent(String sessionId) {
        return sessions.putIfAbsent(sessionId, new ActiveChat(sessionId)) == null;
    }

    public void touch(String sessionId) {
        ActiveChat chat = sessions.get(sessionId);
        if (chat != null) chat.lastActivity = Instant.now().toEpochMilli();
    }

    public boolean close(String sessionId) {
        return sessions.remove(sessionId) != null;
    }

    public Map<String, ActiveChat> snapshot() {
        return Map.copyOf(sessions);
    }
}